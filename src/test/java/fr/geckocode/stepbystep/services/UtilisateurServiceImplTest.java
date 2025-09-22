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

/**
 *
 * Classe de test unitaire pour {@link UtilisateurServiceImpl}.
 * Elle utilise Mockito pour simuler les dépendances et JUnit pour valider
 * le comportement du service concernant la gestion des utilisateurs et des rôles
 * */

public class UtilisateurServiceImplTest {

    /** Simulation du repository des utilisateurs. */
    @Mock
    UtilisateurRepository utilisateurRepository;

    /** Simulation du mapper Entités <-> DTOs */
    @Mock
    MapperTool mapperTool;

    /** Simulation du repository des roles. */
    @Mock
    RoleRepository roleRepository;

    /** Simulation du repository des choregraphies de step. */
    @Mock
    ChoregraphieStepRepository choregraphieStepRepository;

    /** Service à tester, injecté avec les dépendances simulées. */
    @InjectMocks
    UtilisateurServiceImpl utilisateurService;

    /** Instance d'utilisateur exemple utilisée dans les tests. */
    private Utilisateur utilisateur;

    /**
     * Méthode exécutée avant chaque test pour initialiser les mocks et créer l'utilisateur d'exemple.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        utilisateur = new Utilisateur(
                1,
                "test",
                "Johnny",
                "JohnnyTest12!",
                "johnny@test.com",
                null,
                new ArrayList<>(List.of(new Role(NomRole.USER)))
        );
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#obtenirUtilisateur(Integer)}} retourne
     * l'utilisateur quand il existe en base.
     */

    @Test
    void testObtenirUtilisateur_Success() {
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        Utilisateur result = utilisateurService.obtenirUtilisateur(1);

        assertNotNull(result);
        assertEquals(utilisateur.getIdUtilisateur(), result.getIdUtilisateur());
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#obtenirUtilisateur(Integer)} lève une
     * exception {@link UtilisateurNonTrouveException} si l'utilisateur n'existe pas.
     */
    @Test
    void testObtenirUtilisateur_notFound() {
        when(utilisateurRepository.findById(18)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UtilisateurNonTrouveException.class,
                () -> utilisateurService.obtenirUtilisateur(18));

        assertTrue(exception.getMessage().contains("identifiant 18 non trouvé"));
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#obtenirListeUtilisateur()} retourne
     * la liste transformée en DTO quand des utilisateurs existent.
     */
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


    /**
     * Vérifie que {@link UtilisateurServiceImpl#obtenirListeUtilisateur()} retourne une liste vide
     * quand il n’existe aucun utilisateur.
     */
    @Test
    void obtenirListeUtilisateurs_empty() {
        when(utilisateurRepository.findAll()).thenReturn(Collections.emptyList());

        List<UtilisateurDTO> result = utilisateurService.obtenirListeUtilisateur();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#supprimerUtilisateur(Integer)}}
     * appelle la méthode de suppression du repository.
     */

    @Test
    void supprimerUtilisateur_appelleRepository() {
        utilisateurService.supprimerUtilisateur(1);
        verify(utilisateurRepository, times(1)).deleteById(1);
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#ajouterRole(Role)} sauvegarde et retourne le rôle.
     */
    @Test
    void ajouterRole_retourneRoleSauvegarde() {
        Role role = new Role(NomRole.ADMIN);

        when(roleRepository.save(role)).thenReturn(role);

        Role result = utilisateurService.ajouterRole(role);

        assertEquals(NomRole.ADMIN, result.getNomRole());
        verify(roleRepository).save(role);
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#ajouterRoleUtilisateur(Utilisateur, List)}
     * ajoute le rôle à l'utilisateur et sauvegarde la modification.
     */
    @Test
    void ajouterRoleUtilisateur_ajouteDesRoles() {
        Role role = new Role(NomRole.ADMIN);
        Role roleEnBase = new Role(NomRole.ADMIN);

        when(utilisateurRepository.findByEmail(utilisateur.getEmail()))
                .thenReturn(Optional.of(utilisateur));
        when(roleRepository.findByNomRole(NomRole.ADMIN)).thenReturn(Optional.of(roleEnBase));

        utilisateurService.ajouterRoleUtilisateur(utilisateur, Collections.singletonList(role));

        assertTrue(utilisateur.getRoles().contains(roleEnBase));
        verify(utilisateurRepository).save(utilisateur);
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#ajouterRoleUtilisateur(Utilisateur, List)}
     * lance une exception si l'utilisateur n'est pas trouvé.
     */
    @Test
    void ajouterRoleUtilisateur_utilisateurNonTrouve() {
        when(utilisateurRepository.findByEmail(utilisateur.getEmail())).thenReturn(Optional.empty());

        assertThrows(UtilisateurNonTrouveException.class,
                () -> utilisateurService.ajouterRoleUtilisateur(utilisateur,
                        List.of(new Role(NomRole.ADMIN))));
    }

    /**
     * Vérifie que {@link UtilisateurServiceImpl#loadUserByUsername(String)}
     * retourne l’utilisateur existant sous forme de {@link UserDetails}.
     */
    @Test
    void loadUserByUsername_existe() {
        when(utilisateurRepository.findByEmail(utilisateur.getEmail())).thenReturn(Optional.of(utilisateur));

        UserDetails userDetails = utilisateurService.loadUserByUsername(utilisateur.getEmail());

        assertEquals(utilisateur.getEmail(), userDetails.getUsername());
        assertEquals(utilisateur.getMotDePasse(), userDetails.getPassword()); // 🔥 correction ici
        assertTrue(userDetails.getAuthorities().isEmpty());
    }


    /**
     * Vérifie que {@link UtilisateurServiceImpl#loadUserByUsername(String)}
     * lance une exception {@link UsernameNotFoundException} si l'utilisateur n'est pas trouvé.
     */
    @Test
    void loadUserByUsername_inexistant() {
        when(utilisateurRepository.findByEmail("inconnu@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> utilisateurService.loadUserByUsername("inconnu@gmail.com"));
    }
}
