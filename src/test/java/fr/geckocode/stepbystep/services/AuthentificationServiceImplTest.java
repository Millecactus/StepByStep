package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurReponseDTO;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import fr.geckocode.stepbystep.securite.CustomPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthentificationServiceImplTest {

    // ETAPE 1 : je remplace les @autowired par des mocks
    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    MapperTool mapperTool;

    @Mock
    CustomPasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    // 2- je linke la classe de test a la classe qu'elle test avec @InjectMock

    @InjectMocks
    AuthentificationServiceImpl authentificationService;



    Utilisateur utilisateur;


    // 3 -  J'initialise un Setup
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setEmail("guigui@gmail.com");
        utilisateur.setMotDePasse("mot2passe");
    }


    // 4- je creer mes test de methodes

    @Test
    public void shouldReturnAnUser_ConnexionSucess(){
        //ARRANGE

        //Les informations que doit saisir l'utilisateur qui souhaite se connecter
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("guigui@gmail.com")
                .motDePasse("guigui12")
                .build();

        //simulation des composants entrant en interaction avec le methode
        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.of(utilisateur));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getMotDePasse()));

        //ACT (recupere la methode)
        ResponseEntity<UtilisateurReponseDTO> result = authentificationService.connexion(dto);

        //ASSERT
        assertEquals("Utilisateur authentifié avec succes ! ", result.getBody().getMessage());
        assertEquals(HttpStatus.OK, result.getStatusCode());

    }

    @Test
    public void shouldReturnABadRequest_Connexion_UserNotFound(){

        // Données attendues en paramètre de la méthode testée

        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("guigui@gmail.com")
                .motDePasse("guigui")
                .build();

        // simulation des composants
        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.empty());


        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authentificationService.connexion(dto)
        );
        assertEquals("Utilisateur non trouvé", exception.getMessage());

    }

    @Test
    public void ShouldReturnAnUser_Inscription_Success(){
        UtilisateurDTO request = UtilisateurDTO.builder()
                .motDePasse("1234")
                .email("gerard@gmail.com")
                .build();

        when(utilisateurRepository.findByEmail("gerard@gmail.com")).thenReturn(Optional.empty());
        when(mapperTool.convertirDtoEnEntite(any(UtilisateurDTO.class), eq(Utilisateur.class))).thenReturn(utilisateur);
        when(passwordEncoder.encode("1234")).thenReturn("1234");

        ResponseEntity<UtilisateurReponseDTO> result =  authentificationService.inscription(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


























//    @Test
//    void inscription_ShouldReturnSuccess_WhenEmailNotUsed() {
//        // ARRANGE
//        // Préparation des données d’entrée :
//        //
//        //On crée un objet UtilisateurDTO représentant ce que l’utilisateur
//        //soumettrait lors d’une inscription (ici, un email et un mot de passe).
//        UtilisateurDTO dto = UtilisateurDTO.builder()
//                .email("guigui@gmail.com")
//                .motDePasse("mdp123")
//                .build();
//
//        //→ Simulation du repository :
//        //
//        //Avec Mockito, on précise que lorsqu’on cherche un utilisateur
//        // avec cet email, la base ne renvoie rien (Optional.empty()),
//        // ce qui indique que l’email est disponible
//        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.empty());
//        //Simulation du mapping :
//        //
//        //On indique que le mapper doit convertir n'importe quel DTO fourni en utilisateur, et retourner une instance fictive (utilisateur). Cela permet de ne pas tester le vrai mapper ici.
//        when(mapperTool.convertirDtoEnEntite(any(UtilisateurDTO.class), eq(Utilisateur.class)))
//                .thenReturn(utilisateur);
//        // Simulation de l’encodeur de mot de passe
//        // Peu importe la chaîne passée à l’encodeur (le mot de passe), il retournera toujours "mdp-encode" pour simplifier le test.
//        when(passwordEncoder.encode(anyString())).thenReturn("mdp-encode");
//        //→ Simulation de l'enregistrement en base :
//        // Lorsqu’on tente d’enregistrer un utilisateur (peu importe lequel), le repository simulé retourne l’instance fournie (utilisateur).
//        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
//        //→ Exécution de la méthode à tester (ACT) :
//        //
//        //    On appelle la méthode d’inscription avec les données préparées, et on stocke la réponse.
//        ResponseEntity<UtilisateurReponseDTO> response = authentificationService.inscription(dto);
//
//        // Assertion du code retour HTTP :
//        //
//        // On vérifie que la réponse HTTP a un code 200 (succès).
//        assertEquals(200, response.getStatusCodeValue());
//        // Assertion du message de la réponse :
//        //
//        // On vérifie que le message retourné est bien celui attendu lors d’une création réussie.
//        assertEquals("Utilsateur crée avec succès ! ", response.getBody().getMessage());
//
//    }


    @Test
    void inscription_ShouldReturnBadRequest_WhenEmailAlreadyExists() {

        // création de ce qu'un utilisateur soumettrais:
        UtilisateurDTO dto = UtilisateurDTO.builder()
                .email("guigui@gmail.com")
                .motDePasse("mdp123")
                .build();


        //simulation
        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.of(utilisateur));


        ResponseEntity<UtilisateurReponseDTO> response = authentificationService.inscription(dto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("un utilisateur existe deja avec cette adresse email", response.getBody().getMessage());
    }













//    @Test
//    void inscription_ShouldReturnBadRequest_WhenEmailAlreadyExists() {
//        UtilisateurDTO dto = UtilisateurDTO.builder()
//                .email("guigui@gmail.com")
//                .motDePasse("mdp123")
//                .build();
//
//        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.of(utilisateur));
//
//        ResponseEntity<UtilisateurReponseDTO> response = authentificationService.inscription(dto);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("un utilisateur existe deja avec cette adresse email", response.getBody().getMessage());
//    }

    @Test
    void connexion_ShouldReturnSuccess_WhenCredentialsAreCorrect() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("guigui@gmail.com")
                .motDePasse("mdp123")
                .build();

        Authentication fakeAuth = mock(Authentication.class);

        when(utilisateurRepository.findByEmail("guigui@gmail.com")).thenReturn(Optional.of(utilisateur));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(fakeAuth);
        when(jwtService.generateToken(any(Utilisateur.class))).thenReturn("fake-jwt-token");

        ResponseEntity<UtilisateurReponseDTO> response = authentificationService.connexion(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Utilisateur authentifié avec succes ! ", response.getBody().getMessage());
        assertTrue(response.getHeaders().get("Authorization").get(0).contains("Bearer"));
    }

    @Test
    void connexion_ShouldThrowException_WhenUserNotFound() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("not-found@gmail.com")
                .motDePasse("mdp123")
                .build();

        when(utilisateurRepository.findByEmail("not-found@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authentificationService.connexion(dto));
    }
}
