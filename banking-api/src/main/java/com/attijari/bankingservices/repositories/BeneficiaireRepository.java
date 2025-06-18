package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.Beneficiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {
    @Query("SELECT b FROM Beneficiaire b WHERE b.client.idClient = :clientId AND b.prenom = :firstname AND b.nom = :lastname")
    List<Beneficiaire> findByClientIdAndNames(@Param("clientId") Long clientId, @Param("firstname") String firstname, @Param("lastname") String lastname);

    @Query("SELECT b FROM Beneficiaire b WHERE b.client.idClient = :clientId AND b.rib = :rib")
    Beneficiaire findByClientIdAndRib(@Param("clientId")Long clientId,@Param("rib") String rib);






}
