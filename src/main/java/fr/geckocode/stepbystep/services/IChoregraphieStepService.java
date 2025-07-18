package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import java.util.List;

public interface IChoregraphieStepService {

    List<ChoregraphieDeStepDTO> obtenirListeChoreghraphieStep();

    ChoregraphieDeStep creationChoregraphieDeStep(ChoregraphieDeStepDTO dto);

    void supprimerChoregraphieParId(Integer id);

    ChoregraphieDeStep obtenirChoregaphieParId(Integer id);
}
