package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.AjouterRolesUtilisateurRequest;
import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurReponseDTO;
import fr.geckocode.stepbystep.enums.NomRole;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.services.IAuthentificationService;
import fr.geckocode.stepbystep.services.IUtilisateurService;
import fr.geckocode.stepbystep.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/utilisateur")
@RequiredArgsConstructor
public class UtilisateurController {

    private final IUtilisateurService utilisateurService;
    private final IAuthentificationService authentificationService;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello world";
    }

    @PostMapping(value = "/inscription", consumes = "application/json")
    public ResponseEntity<UtilisateurReponseDTO> inscription(@RequestBody UtilisateurDTO dto) {
        return authentificationService.inscription(dto);
    }

    @PostMapping (value = "/connexion", consumes = "application/json")
    public ResponseEntity<UtilisateurReponseDTO> connexion (@RequestBody LoginRequestDTO dto){
        return authentificationService.connexion(dto);
    }


    @GetMapping(value = "/obtenir/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateur(@PathVariable Integer id){
        Utilisateur utilisateurObtenu = utilisateurService.obtenirUtilisateur(id);
        return  ResponseEntity.ok(utilisateurObtenu);
    }

    @PostMapping(value ="/supprimer/{id}")
    public void supprimerUtilisateur(@PathVariable Integer id){
        utilisateurService.supprimerUtilisateur(id);
    }

    @GetMapping("/current-id")
    public Integer getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT manquant ou invalide");
        }

        String jwt = authHeader.substring(7);

        // Appel correct à la méthode extractClaim depuis le service JwtService
        return jwtService.extractClaim(jwt, claims -> claims.get("idUtilisateur", Integer.class));
    }


    @PostMapping("/ajouter-roles")
    public ResponseEntity<?> ajouterRolesUtilisateur(@RequestBody AjouterRolesUtilisateurRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        List<Role> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByNomRole(role)) // Pas besoin de valueOf ou String.valueOf
                .collect(Collectors.toList());
        utilisateurService.ajouterRoleUtilisateur(utilisateur, roles);
        return ResponseEntity.ok("Rôles ajoutés à l'utilisateur " + request.getEmail());
    }



    @PostMapping("/deconnexion")
    public ResponseEntity<String> logout() {
        // En stateless, rien à invalider côté backend
        // Le front devra supprimer le token
        return ResponseEntity.ok("Déconnexion effectuée. Merci de supprimer le token côté client.");
    }

}

