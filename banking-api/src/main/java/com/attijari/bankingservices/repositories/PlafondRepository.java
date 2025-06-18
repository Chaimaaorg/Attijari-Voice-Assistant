package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.models.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlafondRepository extends JpaRepository<Plafond,Long> {
    @Query("SELECT p FROM Plafond p WHERE p.carte.idCarte = :idCarte")
    List<Plafond> findByCarte(@Param("idCarte") Long idCarte);
}
