package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.enums.NomRole;
import fr.geckocode.stepbystep.exceptions.UtilisateurNonTrouveException;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UtilisateurServiceImplTest {

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    MapperTool mapperTool;

    @Mock
    RoleRepository roleRepository;

    @Mock
    ChoregraphieStepRepository choregraphieStepRepository;

    @InjectMocks
    UtilisateurServiceImpl utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        utilisateur = new Utilisateur(
                1,
                "Segond",
                "Guillaume",
                "guigui12",
                "guigui@gmail.com",
                null,
                new ArrayList<>(List.of(new Role(NomRole.USER)))
        );
    }

    // --- TESTS EXISTANTS (les tiens) --- //

    @Test
    void testObtenirUtilisateur_Success() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        Utilisateur result = utilisateurService.obtenirUtilisateur(1);

        assertNotNull(result);
        assertEquals(utilisateur.getIdUtilisateur(), result.getIdUtilisateur());
    }

    @Test
    void testObtenirUtilisateur_notFound() {
        when(utilisateurRepository.findById(18)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UtilisateurNonTrouveException.class,
                () -> utilisateurService.obtenirUtilisateur(18));

        assertTrue(exception.getMessage().contains("identifiant 18 non trouvé"));
    }

    @Test
    void obtenirListeUtilisateurs_Success() {
        List<Utilisateur> utilisateurListe = List.of(utilisateur);

        when(utilisateurRepository.findAll()).thenReturn(utilisateurListe);
        when(mapperTool.convertirEntiteEnDto(utilisateur, UtilisateurDTO.class)).thenReturn(
                new UtilisateurDTO(utilisateur.getNom(), utilisateur.getPrenom(),
                        utilisateur.getEmail(), null)
        );

        List<UtilisateurDTO> result = utilisateurService.obtenirListeUtilisateur();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(utilisateur.getNom(), result.get(0).getNom());
    }

    @Test
    void obtenirListeUtilisateurs_empty() {
        when(utilisateurRepository.findAll()).thenReturn(Collections.emptyList());

        List<UtilisateurDTO> result = utilisateurService.obtenirListeUtilisateur();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- NOUVEAUX TESTS AJOUTÉS --- //

    @Test
    void supprimerUtilisateur_appelleRepository() {
        utilisateurService.supprimerUtilisateur(1);
        verify(utilisateurRepository, times(1)).deleteById(1);
    }

    @Test
    void ajouterRole_retourneRoleSauvegarde() {
        Role role = new Role(NomRole.ADMIN);

        when(roleRepository.save(role)).thenReturn(role);

        Role result = utilisateurService.ajouterRole(role);

        assertEquals(NomRole.ADMIN, result.getNomRole());
        verify(roleRepository).save(role);
    }

    @Test
    void ajouterRoleUtilisateur_ajouteDesRoles() {
        Role role = new Role(NomRole.ADMIN);
        Role roleEnBase = new Role(NomRole.ADMIN);

        when(utilisateurRepository.findByEmail(utilisateur.getEmail()))
                .thenReturn(Optional.of(utilisateur));
        when(roleRepository.findByNomRole(NomRole.ADMIN)).thenReturn(roleEnBase);

        utilisateurService.ajouterRoleUtilisateur(utilisateur, Collections.singletonList(role));

        assertTrue(utilisateur.getRoles().contains(roleEnBase));
        verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    void ajouterRoleUtilisateur_utilisateurNonTrouve() {
        when(utilisateurRepository.findByEmail(utilisateur.getEmail())).thenReturn(Optional.empty());

        assertThrows(UtilisateurNonTrouveException.class,
                () -> utilisateurService.ajouterRoleUtilisateur(utilisateur,
                        List.of(new Role(NomRole.ADMIN))));
    }

    @Test
    void loadUserByUsername_existe() {
        when(utilisateurRepository.findByEmail(utilisateur.getEmail())).thenReturn(Optional.of(utilisateur));

        UserDetails userDetails = utilisateurService.loadUserByUsername(utilisateur.getEmail());

        assertEquals(utilisateur.getEmail(), userDetails.getUsername());
        assertEquals(utilisateur.getMotDePasse(), userDetails.getPassword()); // 🔥 correction ici
        assertTrue(userDetails.getAuthorities().isEmpty());
    }


    @Test
    void loadUserByUsername_inexistant() {
        when(utilisateurRepository.findByEmail("inconnu@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> utilisateurService.loadUserByUsername("inconnu@gmail.com"));
    }
}
