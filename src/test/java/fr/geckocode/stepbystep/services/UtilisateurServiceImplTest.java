package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.enums.NomRole;
import fr.geckocode.stepbystep.exceptions.UtilisateurNonTrouveException;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UtilisateurServiceImplTest {

    // On simule la dépendance UtilisateurRepository pour isoler le service à tester
    @Mock
    UtilisateurRepository utilisateurRepository;

    // Mockito injecte automatiquement le mock (repo simulé) dans le service
    @InjectMocks
    UtilisateurServiceImpl utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        /*
         * initialise tous les mocks pour chaque test,
         * ce qui évite de les initialiser individuellement dans toutes les méthodes.
         */
        MockitoAnnotations.openMocks(this);

        //initialise un utilisateur
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

    @Test
    void testObtenirUtilisateur_Success(){
        // phase de simulation : utilisateur existe dans le repo
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));

        // appel de le methode a tester
        Utilisateur result = utilisateurService.obtenirUtilisateur(1);

        // assertions : Proposition que l'on avance et que l'on soutient comme vraie.
        assertNotNull(result);
        assertEquals(utilisateur.getIdUtilisateur(), result.getIdUtilisateur());
    }

    @Test
    void testObtenirUtilisateur_notFound(){

        //ARRANGE
        when(utilisateurRepository.findById(20)).thenReturn(Optional.empty());

        //ACT
        Utilisateur result = utilisateurService.obtenirUtilisateur(20);

        //ASSERT
        Exception exception =  assertThrows(UtilisateurNonTrouveException.class, () -> utilisateurService.obtenirUtilisateur(2));
        assertTrue(exception.getMessage().contains("identifiant 2 non trouvé"));
    }

    @Test
    void obtenirListeUtilisateurs_Success(){

        List<Utilisateur> utilisateurListe = List.of(utilisateur);

        // On simule le repository pour retourner cette liste
        when(utilisateurRepository.findAll()).thenReturn(utilisateurListe);


        List<UtilisateurDTO> result = utilisateurService.obtenirListeUtilisateur();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(utilisateur.getNom(), result.get(0).getNom());

    }

    @Test
    void obtenirListeUtilisateurs_notFound(){

        List<Utilisateur> utilisateurListe = List.of(utilisateur);

        // On simule le repository pour retourner cette liste
        when(utilisateurRepository.findAll()).thenReturn(Collections.emptyList());


        Exception exception =  assertThrows(UsernameNotFoundException.class, () -> utilisateurService.obtenirListeUtilisateur());
        assertTrue(exception.getMessage().contains("La liste est vide"));
    }

}
