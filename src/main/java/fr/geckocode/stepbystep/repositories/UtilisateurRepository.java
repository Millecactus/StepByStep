package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    // JPA : Equivaut a SELECT FROM utilisateur WHERE email = @email
    Optional <Utilisateur> findByEmail(String email);

    // JPA : Equivaut a SELECT FROM utilisateur WHERE email = @nom
    Utilisateur findByNom(String nom);
}
