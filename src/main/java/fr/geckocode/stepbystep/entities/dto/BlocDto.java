package fr.geckocode.stepbystep.entities.dto;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlocDto {

    private Byte idBloc;
    private Byte nombreDeTemps;
    private Byte ordre;
    private Boolean bloc_symetrique;
    private ChoregraphieDeStep choregraphieDeStep;
    private List<MouvementDTO> listeMouvements = new ArrayList<>();

}
