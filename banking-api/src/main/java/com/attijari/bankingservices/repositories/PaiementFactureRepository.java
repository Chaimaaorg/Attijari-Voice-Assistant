package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.PaiementFacture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementFactureRepository extends JpaRepository<PaiementFacture,Long> {
}
