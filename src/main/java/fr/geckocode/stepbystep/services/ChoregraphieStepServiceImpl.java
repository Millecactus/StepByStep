package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import fr.geckocode.stepbystep.exceptions.ChoregraphieNonTrouveException;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.services.IChoregraphieStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoregraphieStepServiceImpl implements IChoregraphieStepService {

    private final ChoregraphieStepRepository repository;
    private final MapperTool mapperTool;


    @Override
    public List<ChoregraphieDeStepDTO> obtenirListeChoreghraphieStep() {
        return List.of();
    }

    @Override
    public ChoregraphieDeStep creationChoregraphieDeStep(ChoregraphieDeStepDTO dto) {
        ChoregraphieDeStep choregraphieDeStep = mapperTool.convertirDtoEnEntite(dto, ChoregraphieDeStep.class);
        return repository.save(choregraphieDeStep);
    }

    @Override
    public void supprimerChoregraphieParId(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public ChoregraphieDeStep obtenirChoregaphieParId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChoregraphieNonTrouveException("Chorégraphie avec l'identifiant " + id + " non trouvée"));
    }
}
