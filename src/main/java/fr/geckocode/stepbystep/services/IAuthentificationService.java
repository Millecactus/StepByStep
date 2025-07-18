package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurReponseDTO;
import org.springframework.http.ResponseEntity;

public interface IAuthentificationService {

    ResponseEntity<UtilisateurReponseDTO> inscription(UtilisateurDTO dto);

    ResponseEntity <UtilisateurReponseDTO> connexion(LoginRequestDTO dto);
}
