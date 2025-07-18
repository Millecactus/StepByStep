package fr.geckocode.stepbystep.entities.dto;

import fr.geckocode.stepbystep.enums.NomRole;
import lombok.Data;

import java.util.List;
@Data

public class AjouterRolesUtilisateurRequest {

        private String email; // ou Integer idUtilisateur, selon ton choix
        private List<NomRole> roles; // On peut aussi utiliser List<String> ou List<Role>
}
