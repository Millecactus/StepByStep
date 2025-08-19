package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChoregraphieStepRepository extends JpaRepository <ChoregraphieDeStep, Integer> {

    List<ChoregraphieDeStep> findByUtilisateurIdUtilisateur(Integer id);

}
