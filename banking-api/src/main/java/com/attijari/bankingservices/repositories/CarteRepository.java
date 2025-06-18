package com.attijari.bankingservices.repositories;


import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.models.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarteRepository extends JpaRepository<Carte,Long> {

    @Query("SELECT c FROM Carte c WHERE c.numeroCarte = :numeroCarte")
    Optional<Carte> findByNumeroCarte(@Param("numeroCarte") Long numeroCarte);

    @Query("SELECT c FROM Carte c WHERE c.typeCarte = :typeCarte")
    List<Carte> findByTypeCarte(@Param("typeCarte") String typeCarte);
}
