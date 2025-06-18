package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.Agence;
import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.AgenceRepository;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.AgenceService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AgenceServiceImpl implements AgenceService {

    private static final double MAX_DISTANCE = 10000.0;
    private final AgenceRepository agenceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;

    public AgenceServiceImpl(AgenceRepository agenceRepository, UtilisateurRepository utilisateurRepository, CompteRepository compteRepository) {
        this.agenceRepository = agenceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
    }

    @Override
    public List<Agence> getAllAgencies() {
        return agenceRepository.findAll();
    }

    @Override
    public List<Agence> getAgenciesByUserId(Long userId) {
        List<Agence> agencies = new ArrayList<>();

        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur != null) {
            Client client = utilisateur.getClient();
            if (client != null) {
                Long clientId = client.getIdClient();
                List<Compte> accounts = compteRepository.findByClientId(clientId);
                for (Compte account : accounts) {
                    Agence agence = account.getAgence();
                    if (agence != null) {
                        agencies.add(agence);
                    }
                }
            }
        }
        return agencies;
    }

    @Override
    public List<Agence> getAgenciesByRegistredAddress(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        String address = user.getClient().getAdresse();
        try {
            double[] LatLon = getLatLonFromAddress(address);
            if (LatLon != null) {
                BigDecimal latitude = BigDecimal.valueOf(LatLon[0]);
                BigDecimal longitude = BigDecimal.valueOf(LatLon[1]);
                return getAgenciesByUserLocation(latitude, longitude);
            } else {
                // Handle invalid address or API error
                return Collections.emptyList();
            }
        } catch (Exception e) {
            // Handle API errors or network issues
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private double[] getLatLonFromAddress(String address) throws IOException {
        String formattedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
        String urlString = "https://nominatim.openstreetmap.org/search?q=" + formattedAddress + "&format=jsonv2";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                double lat = jsonObject.optDouble("lat");
                double lon = jsonObject.optDouble("lon");
                return new double[]{lat, lon};
            } else {
                return null;
            }
        }
    }
//    private double[] getLatLonFromAddress(String address) throws IOException {
//        String formattedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
//        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + formattedAddress + "&key=AIzaSyC35AEd7iiqLVsbPOog4VUk3VlbuJX61II";
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//
//            JSONObject jsonObject = new JSONObject(response.toString());
//            if (jsonObject.getString("status").equals("OK")) {
//                JSONArray results = jsonObject.getJSONArray("results");
//                if (results.length() > 0) {
//                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
//                    JSONObject location = geometry.getJSONObject("location");
//                    double lat = location.getDouble("lat");
//                    double lng = location.getDouble("lng");
//                    return new double[]{lat, lng};
//                }
//            }
//            return null;
//        }
//    }

    @Override
    public List<Agence> getAgenciesByUserLocation(BigDecimal lat, BigDecimal lng) {
        List<Agence> nearestAgencies = new ArrayList<>();
        for (Agence agence : agenceRepository.findAll()) {
            double[] LatLon = new double[0];
            try {
                LatLon = getLatLonFromAddress(agence.getAdresse());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (LatLon != null) {
                double latDiff = lat.doubleValue() - LatLon[0];
                double lonDiff = lng.doubleValue() - LatLon[1];

                double distance = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
                if (distance <= MAX_DISTANCE) {
                    nearestAgencies.add(agence);
                }
            }
        }

        return nearestAgencies;
    }
}
