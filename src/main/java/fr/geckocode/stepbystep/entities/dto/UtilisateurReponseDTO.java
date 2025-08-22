package fr.geckocode.stepbystep.entities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilisateurReponseDTO {
    public String message;
//    private String email;
    public String token;
    public UtilisateurDTOSansMdp utilisateur;

}
