package fr.geckocode.stepbystep.entities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilisateurReponseDTO {
    private String message;
//    private String email;
    private String token;
    private UtilisateurDTOSansMdp utilisateur;

}
