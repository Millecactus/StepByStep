package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTOSansMdp;
import fr.geckocode.stepbystep.entities.dto.UtilisateurReponseDTO;
import fr.geckocode.stepbystep.enums.NomRole;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import fr.geckocode.stepbystep.securite.CustomPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthentificationServiceImpl implements IAuthentificationService {


    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final MapperTool mapperTool;
    private final CustomPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ResponseEntity<UtilisateurReponseDTO> inscription(UtilisateurDTO dto) {
        //Si l'adresse mail est deja present dans la base de donnees :
        if(utilisateurRepository.findByEmail(dto.getEmail()).isPresent()){
            //renvoit un code badRequest en reponse http avec un message
            return ResponseEntity.badRequest()
                    .body(UtilisateurReponseDTO.builder()
                            .message("un utilisateur existe deja avec cette adresse email")
                            .build());
        }
        // conversion Entite en DTO
        Utilisateur utilisateur = mapperTool.convertirDtoEnEntite(dto, Utilisateur.class);
        // Encodage Mot de passe
        String motDePasseEncode = passwordEncoder.encode(dto.getMotDePasse());
        //Application du mot de apsse encode
        utilisateur.setMotDePasse(motDePasseEncode);

        // Récupère le rôle USER
        Role roleUser = roleRepository.findByNomRole(NomRole.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Role USER non trouvé"));
        utilisateur.setRoles(Collections.singletonList(roleUser));

        //persiste en db
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(
                UtilisateurReponseDTO.builder()
                        .message("Utilisateur crée avec succès !")
                        .build());
    }

    public ResponseEntity<UtilisateurReponseDTO> connexion(LoginRequestDTO dto) {

            //On verifie si l'email renseigne existe en bd
            var utilisateur = utilisateurRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new UsernameNotFoundException ("Utilisateur non trouvé"));
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getMotDePasse()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Creation du token jwt
            var jwtToken = jwtService.generateToken(utilisateur);

            //renvoie du utilisateur dans l'entete (header) de la reponse
            HttpHeaders reponseEntete = new  HttpHeaders();

            //quqnd on veut envoyer des en tete personnalisés,
            // il faut s'assurer que l'autorisation CORS le permet
            reponseEntete.add("Access-Control-Expose-Header", "Authorization");
            reponseEntete.add( "Authorization", "Bearer "+ jwtToken);

            return  ResponseEntity.ok()
                    .headers(reponseEntete)
                    .body(UtilisateurReponseDTO.builder()
                            .message("Utilisateur authentifié avec succes ! ")
                            .token(jwtToken)
                            .utilisateur(mapperTool.convertirEntiteEnDto(utilisateur, UtilisateurDTOSansMdp.class))
                            .build());
    }

}


