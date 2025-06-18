package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    @Query("SELECT f FROM Operation f WHERE f.compte = :compte")
    List<Operation> findByCompte(@Param("compte") Compte compte);

    default List<Operation> findByClientIdAndEntities(Long clientId, Map<String, Object> entitiesDict, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Operation> criteriaQuery = criteriaBuilder.createQuery(Operation.class);
        Root<Operation> root = criteriaQuery.from(Operation.class);

        // Join the Compte entity for accurate filtering
        Join<Operation, Compte> compteJoin = root.join("compte");

        // Join the Client entity
        Join<Compte, Client> clientJoin = compteJoin.join("client");

        Predicate clientIdPredicate = criteriaBuilder.equal(clientJoin.get("idClient"), clientId);
        Predicate attributePredicates = buildAttributePredicates(criteriaBuilder, root, entitiesDict);

        criteriaQuery.where(criteriaBuilder.and(clientIdPredicate, attributePredicates));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    default Predicate buildAttributePredicates(CriteriaBuilder criteriaBuilder, Root<Operation> root, Map<String, Object> entitiesDict) {
        Predicate[] predicates = entitiesDict.entrySet().stream()
                .map(entry -> criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue()))
                .toArray(Predicate[]::new);
        return criteriaBuilder.and(predicates);
    }
}
