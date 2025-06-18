package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.services.UtilisateurService;
import com.attijari.bankingservices.utils.MultipartFileResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/users")
@Tag(name = "Utilisateur",description = "Gestion des utilisateurs")
public class UtilisateurController {

    @Autowired
    private  UtilisateurService utilisateurService;
    @GetMapping("/authenticate")
    @Operation(summary = "Méthode pour authentifier les utilisateurs")
    public ResponseEntity<Utilisateur> authenticateUser(@RequestParam String username, @RequestParam String password) {
        Utilisateur utilisateur = utilisateurService.authenticateUser(username, password);
        if (utilisateur != null) {
            return ResponseEntity.ok(utilisateur);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Méthode pour récupérer un utilisateur à partir de son id")
    public ResponseEntity<Utilisateur> getUserById(@PathVariable Long userId) {
        Utilisateur utilisateur = utilisateurService.getUserById(userId);
        if (utilisateur != null) {
            return ResponseEntity.ok(utilisateur);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/authenticate-voice-print")
    public ResponseEntity<Utilisateur> compareAudio(@RequestParam("audio") MultipartFile audio) {
        try {
            // Retrieve all users from the database
            List<Utilisateur> utilisateurs = utilisateurService.getAllUsers();

            // Save the uploaded file to a temporary location
            File tempFile = saveMultipartFile(audio);

            // Iterate over each user and compare audio files
            Long mostSimilarUserId = null;
            Utilisateur mostSimilarUser = null;
            double lowestDistance = Double.MAX_VALUE;  // Initialize with a high value

            for (Utilisateur utilisateur : utilisateurs) {
                String audioFilePath = utilisateur.getCheminAudio();
                // Call FastAPI for audio comparison
                double distance = compareWithFastAPI(tempFile, audioFilePath);

                if (distance < lowestDistance) {  // Compare for the lowest distance
                    lowestDistance = distance;
                    mostSimilarUserId = utilisateur.getIdUser();
                    mostSimilarUser = utilisateur;
                }
            }

            // Delete the temporary file
            tempFile.delete();

            if (mostSimilarUserId != null) {
                return ResponseEntity.ok(mostSimilarUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private File saveMultipartFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

    private double compareWithFastAPI(File audioFile, String audioFilePath) {
        RestTemplate restTemplate = new RestTemplate();
        String fastAPIUrl = "http://localhost:8002/";

        // Prepare request payload
        Resource originalAudioResource = new FileSystemResource(audioFile);
        Resource userAudioResource = new FileSystemResource(audioFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("original_audio", originalAudioResource);
        body.add("user_audio", userAudioResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send request to FastAPI and get the response
        ResponseEntity<Double> response = restTemplate.postForEntity(fastAPIUrl, requestEntity, Double.class);
        return response.getBody() != null ? response.getBody() : 0.0;
    }
}
