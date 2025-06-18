package com.attijari.bankingservices.repositories;
import com.attijari.bankingservices.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
    @Query("SELECT u FROM Utilisateur u WHERE u.username = :username")
    Utilisateur findByUsername(@Param("username") String username);



}
