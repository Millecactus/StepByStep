package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.AjouterRolesUtilisateurRequest;
import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurReponseDTO;
import fr.geckocode.stepbystep.exceptions.ErrorResponse;
import fr.geckocode.stepbystep.exceptions.UtilisateurNonTrouveException;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.services.IAuthentificationService;
import fr.geckocode.stepbystep.services.IUtilisateurService;
import fr.geckocode.stepbystep.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Test")
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello world";
    }

    @Operation(summary = "Inscription", description = "Créer un compte utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inscription réussie",
                content = @Content(schema = @Schema(implementation = UtilisateurDTO.class))),
        @ApiResponse(responseCode = "400", description = "Inscription échouée",
                content = @Content(schema = @Schema(implementation = UtilisateurNonTrouveException.class)))
    })
    @PostMapping(value = "/inscription", consumes = "application/json")
    public ResponseEntity<UtilisateurReponseDTO> inscription(
            @Parameter(description = "Utilisateur", required = true)
            @RequestBody UtilisateurDTO dto) {
        return authentificationService.inscription(dto);
    }

    @PostMapping(value = "/connexion", consumes = "application/json")
    @Operation(summary = "Connexion", description = "Se connecter avec un utilisateur existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie", content = @Content(schema = @Schema(implementation = UtilisateurReponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la connexion", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UtilisateurReponseDTO> connexion(
            @Parameter(description = "Données de connexion utilisateur", required = true)
            @RequestBody LoginRequestDTO dto) {
        return authentificationService.connexion(dto);
    }

    @GetMapping(value = "/obtenir/{id}")
    @Operation(summary = "Obtenir utilisateur", description = "Récupérer un utilisateur par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé", content = @Content(schema = @Schema(implementation = Utilisateur.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la récupération", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Utilisateur> obtenirUtilisateur(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Integer id) {
        Utilisateur utilisateurObtenu = utilisateurService.obtenirUtilisateur(id);
        return ResponseEntity.ok(utilisateurObtenu);
    }

    @PostMapping(value = "/supprimer/{id}")
    @Operation(summary = "Supprimer utilisateur", description = "Supprimer un utilisateur par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la suppression", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void supprimerUtilisateur(
            @Parameter(description = "ID de l'utilisateur à supprimer", required = true)
            @PathVariable Integer id) {
        utilisateurService.supprimerUtilisateur(id);
    }

    @GetMapping("/current-id")
    @Operation(summary = "ID utilisateur courant", description = "Récupère l'ID de l'utilisateur connecté à partir du token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ID utilisateur récupéré", content = @Content(schema = @Schema(type = "integer"))),
            @ApiResponse(responseCode = "400", description = "JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Integer getCurrentUserId(
            @Parameter(hidden = true) // Pas besoin de documenter HttpServletRequest
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("JWT manquant ou invalide");
        }
        String jwt = authHeader.substring(7);
        return jwtService.extractClaim(jwt, claims -> claims.get("idUtilisateur", Integer.class));
    }

    @PostMapping("/ajouter-roles")
    @Operation(summary = "Ajouter des rôles", description = "Ajoute des rôles à un utilisateur donné")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rôles ajoutés avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'ajout des rôles", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> ajouterRolesUtilisateur(
            @Parameter(description = "Requête d'ajout de rôles", required = true)
            @RequestBody AjouterRolesUtilisateurRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        List<Role> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByNomRole(role))
                .collect(Collectors.toList());
        utilisateurService.ajouterRoleUtilisateur(utilisateur, roles);
        return ResponseEntity.ok("Rôles ajoutés à l'utilisateur " + request.getEmail());
    }

    @PostMapping("/deconnexion")
    @Operation(summary = "Déconnexion", description = "Déconnecter l'utilisateur (suppression du token côté client)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Déconnexion effectuée")
    })
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Déconnexion effectuée. Merci de supprimer le token côté client.");
    }

}

