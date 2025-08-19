package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.Mouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MouvementRepository extends JpaRepository<Mouvement, Byte> {

    Mouvement findByNom(String nom);

    List<Mouvement> findByNomContainingIgnoreCase(String nom);


}
