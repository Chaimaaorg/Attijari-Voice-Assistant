package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Compte;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    @Query("SELECT c FROM Compte c WHERE c.client.idClient = :clientId")
    List<Compte> findByClientId(@Param("clientId") Long clientId);

    Compte findByNumeroCompte(String numeroCompte);
    default List<Compte> findByClientIdAndEntities(Long clientId, Map<String, Object> entitiesDict, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compte> criteriaQuery = criteriaBuilder.createQuery(Compte.class);
        Root<Compte> root = criteriaQuery.from(Compte.class);

        // Join the Client entity for accurate filtering
        Join<Compte, Client> clientJoin = root.join("client");

        Predicate clientIdPredicate = criteriaBuilder.equal(clientJoin.get("idClient"), clientId);
        Predicate attributePredicates = buildAttributePredicates(criteriaBuilder, root, entitiesDict);

        criteriaQuery.where(criteriaBuilder.and(clientIdPredicate, attributePredicates));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    default Predicate buildAttributePredicates(CriteriaBuilder criteriaBuilder, Root<Compte> root, Map<String, Object> entitiesDict) {
        Predicate[] predicates = entitiesDict.entrySet().stream()
                .map(entry -> criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue()))
                .toArray(Predicate[]::new);
        return criteriaBuilder.and(predicates);
    }

}
