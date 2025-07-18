package fr.geckocode.stepbystep.entities.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoregraphieDeStepDTO {

    @Size(max = 50)
    private String nomScript;

    private Short nombreTotalMouvement;

    private boolean scriptSymetrique;

    @Size (max = 50)
    private String niveau;

    private Short dureeTotale;

    @NonNull
    private LocalDate dateCreation;

}
