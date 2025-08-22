package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Bloc;
import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Mouvement;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.BlocDto;
import fr.geckocode.stepbystep.entities.dto.MouvementDTO;
import fr.geckocode.stepbystep.repositories.BlocRepository;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.MouvementRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlocServiceImplTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private MouvementRepository mouvementRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ChoregraphieStepRepository choregraphieRepository;

    @Mock
    private ChoregraphieStepServiceImpl choregraphieStepService;

    @InjectMocks
    private BlocServiceImpl blocService;

    private Utilisateur utilisateur;
    private ChoregraphieDeStep dtoChoreo;
    private BlocDto blocDto;

    @BeforeEach
    void setup() {
        // Préparer un utilisateur avec un ID
        utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(1);

        // Préparer un DTO de chorégraphie pour le test
        dtoChoreo = new ChoregraphieDeStep();
        dtoChoreo.setNomScript("scriptTest");
        dtoChoreo.setNombreTotalMouvement((short) 5);
        dtoChoreo.setNiveau(String.valueOf(1));
        dtoChoreo.setDureeTotale((short) 300);
        dtoChoreo.setDateCreation(LocalDate.now());
        dtoChoreo.setTitre("titreTest");
        dtoChoreo.setBpm(120);
        dtoChoreo.setUtilisateur(utilisateur);

        // Préparer le BlocDto
        blocDto = new BlocDto();
        blocDto.setNombreDeTemps((byte) 4);
        blocDto.setOrdre((byte) 1);
        blocDto.setBloc_symetrique(false);
        blocDto.setChoregraphieDeStep(dtoChoreo);

        // Ajouter mouvements DTO
        MouvementDTO mouvDto = new MouvementDTO();
        mouvDto.setIdMouvement((byte) 10);
        blocDto.setListeMouvements(List.of(mouvDto));
    }

    @Test
    void testCreationBloc_Success() {
        // Simuler le repository utilisateur
        when(utilisateurRepository.findById(utilisateur.getIdUtilisateur()))
                .thenReturn(Optional.of(utilisateur));

        // Simuler le repository choregraphie pour sauver et retourner un DTO modifié (avec ID)
        ChoregraphieDeStep savedChoreo = new ChoregraphieDeStep();
        savedChoreo.setIdCours(100);
        when(choregraphieRepository.save(any(ChoregraphieDeStep.class)))
                .thenReturn(savedChoreo);

        // Simuler le mouvement repository findById
        Mouvement mouv = new Mouvement();
        mouv.setIdMouvement((byte) 10);
        when(mouvementRepository.findById((byte) 10)).thenReturn(Optional.of(mouv));

        // 👉 Important : renvoyer le même bloc enrichi, pas un stub vide
        when(blocRepository.save(any(Bloc.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Appeler la méthode à tester
        Bloc resultBloc = blocService.creationBloc(blocDto);

        // Vérifier interactions
        verify(utilisateurRepository).findById(utilisateur.getIdUtilisateur());
        verify(choregraphieRepository).save(any(ChoregraphieDeStep.class));
        verify(mouvementRepository).findById((byte)10);
        verify(blocRepository).save(any(Bloc.class));

        // Vérifier que le bloc contient bien le mouvement
        assertNotNull(resultBloc.getListeMouvements());
        assertEquals(1, resultBloc.getListeMouvements().size());
        assertTrue(resultBloc.getListeMouvements().contains(mouv));

        // Vérifier que le mouvement est lié au bloc
        assertSame(resultBloc, mouv.getBloc());
    }


    @Test
    void testCreationBloc_UtilisateurIntrouvable() {
        when(utilisateurRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> blocService.creationBloc(blocDto));
        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void testCreationBloc_MouvementIntrouvable() {
        when(utilisateurRepository.findById(utilisateur.getIdUtilisateur())).thenReturn(Optional.of(utilisateur));
        when(choregraphieRepository.save(any())).thenReturn(dtoChoreo);
        when(mouvementRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> blocService.creationBloc(blocDto));
        assertTrue(exception.getMessage().startsWith("Mouvement introuvable"));
    }
}
