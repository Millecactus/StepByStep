package fr.geckocode.stepbystep.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTOSansMdp {

    // DTO output sans Mot de passe
    public String nom;
    public String prenom;
    public String email;


}
