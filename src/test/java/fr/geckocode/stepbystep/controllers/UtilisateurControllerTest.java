package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.*;
import fr.geckocode.stepbystep.enums.NomRole;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.services.IAuthentificationService;
import fr.geckocode.stepbystep.services.IUtilisateurService;
import fr.geckocode.stepbystep.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Classe de tests unitaires pour {@link UtilisateurController}.
 **/
class UtilisateurControllerTest {
    /** Simulation du service de gestion des utilisateurs. */
    @Mock
    private IUtilisateurService utilisateurService;

    /** Simulation du service d'authentification. */
    @Mock
    private IAuthentificationService authentificationService;

    /** Simulation du repository des roles. */
    @Mock
    private RoleRepository roleRepository;

    /** Simulation du service de gestion des JWT. */
    @Mock
    private JwtService jwtService;

    /** Contrôleur testé avec injection des mocks précédents. */
    @InjectMocks
    private UtilisateurController utilisateurController;
    private Utilisateur utilisateur;
    private UtilisateurDTO utilisateurDTO;
    private UtilisateurReponseDTO utilisateurReponseDTO;
    private LoginRequestDTO loginRequest;

    /**
     * Initialise les objets et les mocks avant chaque test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(1);
        utilisateur.setEmail("test@test.com");

        utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setEmail("test@test.com");
        utilisateurDTO.setNom("Nom");
        utilisateurDTO.setPrenom("Prenom");
        utilisateurDTO.setMotDePasse("mdp");

        utilisateurReponseDTO = UtilisateurReponseDTO.builder()
                .message("Message ici")
                .token("fake-jwt")
                .utilisateur(UtilisateurDTOSansMdp.builder()
                        .nom("Nom")
                        .prenom("Prenom")
                        .email("test@test.com")
                        .build())
                .build();
    }


    /**
     * Vérifie que l'endpoint sayHello retourne bien "Hello world".
     */
    @Test
    void testSayHello() {
        String result = utilisateurController.sayHello();
        assertEquals("Hello world", result);
    }

    /**
     * Teste que l'inscription via le contrôleur
     * appelle le service d'authentification et retourne la bonne réponse.
     */
    @Test
    void testInscription() {
        when(authentificationService.inscription(any(UtilisateurDTO.class)))
                .thenReturn(ResponseEntity.ok(utilisateurReponseDTO));

        ResponseEntity<UtilisateurReponseDTO> response = utilisateurController.inscription(utilisateurDTO);

        assertNotNull(response);
        assertEquals("test@test.com", response.getBody().getUtilisateur().getEmail());
        verify(authentificationService, times(1)).inscription(utilisateurDTO);
    }


    /**
     * Teste la connexion d'un utilisateur avec le service d'authentification.
     */
    @Test
    void testConnexion() {

        loginRequest = LoginRequestDTO.builder()
                .motDePasse("test@test.com")
                .email("mdp")
                .build();

        when(authentificationService.connexion(loginRequest))
                .thenReturn(ResponseEntity.ok(utilisateurReponseDTO));

        ResponseEntity<UtilisateurReponseDTO> response = utilisateurController.connexion(loginRequest);

        assertEquals("fake-jwt", response.getBody().getToken());
        verify(authentificationService, times(1)).connexion(loginRequest);
    }

    /**
     * Teste la récupération d'un utilisateur spécifique selon son identifiant.
     */
    @Test
    void testObtenirUtilisateur() {
        when(utilisateurService.obtenirUtilisateur(1)).thenReturn(utilisateur);

        ResponseEntity<Utilisateur> response = utilisateurController.obtenirUtilisateur(1);

        assertEquals(1, response.getBody().getIdUtilisateur());
        assertEquals("test@test.com", response.getBody().getEmail());
        verify(utilisateurService).obtenirUtilisateur(1);
    }

    /**
     * Vérifie que la suppression d'un utilisateur appelle le service adéquat.
     */
    @Test
    void testSupprimerUtilisateur() {
        doNothing().when(utilisateurService).supprimerUtilisateur(1);

        utilisateurController.supprimerUtilisateur(1);
        verify(utilisateurService).supprimerUtilisateur(1);
    }

    /**
     * Vérifie l'extraction de l'identifiant utilisateur depuis un JWT contenu dans la requête HTTP.
     */
    @Test
    void testGetCurrentUserId_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer fake-jwt");
        when(jwtService.extractClaim(eq("fake-jwt"), any(Function.class))).thenReturn(42);

        Integer userId = utilisateurController.getCurrentUserId(request);

        assertEquals(42, userId);
        verify(jwtService).extractClaim(eq("fake-jwt"), any(Function.class));
    }

    /**
     * Vérifie la gestion d'une requête sans JWT, attend une exception spécifique.
     */
    @Test
    void testGetCurrentUserId_missingJwt() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> utilisateurController.getCurrentUserId(request));
        assertEquals("JWT manquant ou invalide", ex.getMessage());
    }

    /**
     * Teste l'ajout de plusieurs rôles à un utilisateur
     * et vérifie que chaque rôle est bien traité et que le service métier est correctement appelé.
     */
    @Test
    void ajouterRolesUtilisateur_ShouldAddRolesAndReturnSuccessMessage() {
        // Arrange
        AjouterRolesUtilisateurRequest request = new AjouterRolesUtilisateurRequest();
        request.setEmail("test@test.com");
        request.setRoles(List.of(NomRole.USER, NomRole.ADMIN));

        Role roleUser = new Role();
        roleUser.setNomRole(NomRole.USER);

        Role roleAdmin = new Role();
        roleAdmin.setNomRole(NomRole.ADMIN);

        // Mock des appels
        when(roleRepository.findByNomRole(NomRole.USER)).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByNomRole(NomRole.ADMIN)).thenReturn(Optional.of(roleAdmin));


        // Act
        ResponseEntity<?> response = utilisateurController.ajouterRolesUtilisateur(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Rôles ajoutés à l'utilisateur test@test.com", response.getBody());

        // Vérification des appels
        verify(roleRepository, times(1)).findByNomRole(NomRole.USER);
        verify(roleRepository, times(1)).findByNomRole(NomRole.ADMIN);

        // Vérification que utilisateurService.ajouterRoleUtilisateur a été appelé
        ArgumentCaptor<Utilisateur> utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        ArgumentCaptor<List<Role>> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(utilisateurService, times(1)).ajouterRoleUtilisateur(utilisateurCaptor.capture(), rolesCaptor.capture());

        // Vérification du contenu des arguments capturés
        Utilisateur capturedUtilisateur = utilisateurCaptor.getValue();
        assertEquals("test@test.com", capturedUtilisateur.getEmail());

        List<Role> capturedRoles = rolesCaptor.getValue();
        assertEquals(2, capturedRoles.size());
        assertTrue(capturedRoles.contains(roleUser));
        assertTrue(capturedRoles.contains(roleAdmin));
    }

    /**
     * Vérifie que l'endpoint de déconnexion retourne le bon message et le bon code HTTP.
     */
    @Test
    void testLogout() {
        ResponseEntity<String> response = utilisateurController.logout();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Déconnexion effectuée. Merci de supprimer le token côté client.", response.getBody());
    }
}
