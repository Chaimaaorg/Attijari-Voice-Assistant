package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture,Long> {

    @Query("SELECT f FROM Facture f WHERE f.numeroFacture = :numeroFacture")
    Optional<Facture> findByNumeroFacture(@Param("numeroFacture") String numeroFacture);
}
