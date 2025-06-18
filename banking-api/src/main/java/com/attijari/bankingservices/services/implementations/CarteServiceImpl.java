package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.CarteRepository;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.CarteService;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

@Service
public class CarteServiceImpl implements CarteService {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final CarteRepository carteRepository;

    public CarteServiceImpl(UtilisateurRepository utilisateurRepository, CompteRepository compteRepository, CarteRepository carteRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.carteRepository = carteRepository;
    }

    @Override
    public List<Carte> getCardByUserId(Long userId) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(userId);
        if (utilisateurOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        Long idClient = utilisateurOptional.get().getClient().getIdClient();

        List<Compte> comptes = compteRepository.findByClientId(idClient);

        List<Carte> cartes = carteRepository.findAll();

        List<Carte> result = new ArrayList<>();
        if(comptes!=null){
            for (Compte compte : comptes) {
                for (Carte carte : cartes) {
                    if (carte.getCompte().getIdCompte().equals(compte.getIdCompte())) {
                        result.add(carte);
                    }
                }
            }
            return result;
        }
        return result;
    }

    @Override
    public Optional<Carte> getCardByNum(Long cardNum) {
        return carteRepository.findByNumeroCarte(cardNum);
    }

    @Override
    public List<Carte> getCardByUserIdAndType(Long userId, String typeCard) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(userId);
        if (utilisateurOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        Long idClient = utilisateurOptional.get().getClient().getIdClient();

        List<Compte> comptes = compteRepository.findByClientId(idClient);

        List<Carte> cartes = carteRepository.findAll();

        List<Carte> result = new ArrayList<>();
        for (Compte compte : comptes) {
            for (Carte carte : cartes) {
                if (carte.getCompte().getIdCompte().equals(compte.getIdCompte()) && carte.getTypeCarte().equals(typeCard)) {
                    result.add(carte);
                }
            }
        }
        return result;
    }

    @Override
    public List<Carte> getCardByUserIdAndEntities(Long userId, Map<String, Object> entitiesDict) {
        List<Carte> cartes = getCardByUserId(userId);
        List<Carte> filteredCartes = new ArrayList<>();

        for (Carte carte : cartes) {
            boolean match = true;
            for (Map.Entry<String, Object> entry : entitiesDict.entrySet()) {
                String dbAttributeName = entry.getKey();
                Object attributValue = entry.getValue();

                // Convertir le nom de l'attribut de la base de données en lower camel case
                String javaAttributeName = convertSnakeCaseToCamelCase(dbAttributeName);

                try {
                    // Récupérer la méthode getter correspondante pour l'attribut
                    Method getterMethod = Carte.class.getMethod("get" + capitalize(javaAttributeName));

                    // Appeler la méthode getter sur l'objet Carte
                    Object attributeValue = getterMethod.invoke(carte);

                    // Vérifier si la valeur de l'attribut correspond à la valeur spécifiée dans le dictionnaire
                    if (!attributValue.equals(attributeValue)) {
                        match = false;
                        break;
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // Gérer l'erreur si la méthode getter n'est pas trouvée ou s'il y a un problème d'accès
                    e.printStackTrace();
                    match = false;
                    break;
                }
            }
            if (match) {
                filteredCartes.add(carte);
            }
        }
        return filteredCartes;
    }

    // Méthode pour convertir snake_case en lower camel case
    private String convertSnakeCaseToCamelCase(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();
        String[] parts = snakeCase.split("_");
        camelCase.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return camelCase.toString();
    }

    // Méthode pour capitaliser la première lettre d'une chaîne
    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void updateCardServicesByCardNum(Long userId, Long cardNum, List<String> services, String status) {
        List<Carte> cartes = getCardByUserId(userId);
        for (Carte carte : cartes) {
            if (carte.getNumeroCarte().equals(cardNum)) {
                String existingServices = carte.getServices();
                List<String> existingServiceList = existingServices != null ? new ArrayList<>(Arrays.asList(existingServices.split(","))) : new ArrayList<>();
                if (status.equals("enable")) {
                    for (String service : services) {
                        if (!existingServiceList.contains(service)) {
                            existingServiceList.add(service);
                        }
                    }
                }else if (status.equals("disable")) {
                    existingServiceList.removeAll(services);
                }
                String updatedServices = String.join(",", existingServiceList);
                carte.setServices(updatedServices);
                carteRepository.save(carte);
            }
        }
    }

    @Override
    public void updateCardServicesByCardType(Long userId, String cardType, List<String> services, String status) {
        List<Carte> cartes = getCardByUserIdAndType(userId, cardType);
        Carte carte = cartes.get(0);
        String existingServices = carte.getServices();
        List<String> existingServiceList = existingServices != null ? new ArrayList<>(Arrays.asList(existingServices.split(","))) : new ArrayList<>();
        List<String> servicesToRemove = new ArrayList<>(); // New list to store elements to be removed
        if (status.equals("enable")) {
            existingServiceList.addAll(services);
        } else if (status.equals("disable")) {
            // Iterate through services to be removed and add them to servicesToRemove list
            for (String service : services) {
                if (existingServiceList.contains(service)) {
                    servicesToRemove.add(service);
                }
            }
            // Remove services from existingServiceList
            existingServiceList.removeAll(servicesToRemove);
        }
        String updatedServices = String.join(",", existingServiceList);
        carte.setServices(updatedServices);
        carteRepository.save(carte);
    }


    @Override
    public void opposeCardByCardNum(Long userId, Long cardNum, String reasons) {
        List<Carte> cartes = getCardByUserId(userId);
        for (Carte carte : cartes) {
            if (carte.getNumeroCarte().equals(cardNum)) {
                Timestamp dateOpposition = new Timestamp(System.currentTimeMillis());
                carte.setDateOpposition(dateOpposition);
                carte.setStatutCarte("opposée");
                carte.setRaisonsOpposition(reasons);
                carteRepository.save(carte);
            }
        }
    }

    @Override
    public void opposeCardByCardType(Long userId, String cardType, String reasons) {
        List<Carte> cartes = getCardByUserId(userId);
        for (Carte carte : cartes) {
            if (carte.getTypeCarte().equals(cardType)) {
                Timestamp dateOpposition = new Timestamp(System.currentTimeMillis());
                carte.setDateOpposition(dateOpposition);
                carte.setStatutCarte("opposée");
                carte.setRaisonsOpposition(reasons);
                carteRepository.save(carte);
            }
        }
    }
}
