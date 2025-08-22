package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import fr.geckocode.stepbystep.exceptions.ChoregraphieNonTrouveException;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChoregraphieStepServiceImplTest {

    @Mock
    private ChoregraphieStepRepository repository;

    @Mock
    private MapperTool mapperTool; // pas encore utilisé car obtenirListeChoreghraphieStep est vide

    @InjectMocks
    private ChoregraphieStepServiceImpl choregraphieStepService;

    private ChoregraphieDeStep choregraphie;

    @BeforeEach
    void setUp() {
        choregraphie = new ChoregraphieDeStep();
        choregraphie.setIdCours(1);
        choregraphie.setTitre("Test chorégraphie");
    }

    @Test
    void testCreationChoregraphieDeStep_success() {
        when(repository.save(choregraphie)).thenReturn(choregraphie);

        ChoregraphieDeStep result = choregraphieStepService.creationChoregraphieDeStep(choregraphie);

        assertNotNull(result);
        assertEquals("Test chorégraphie", result.getTitre());
        verify(repository, times(1)).save(choregraphie);
    }

    @Test
    void testSupprimerChoregraphieParId_success() {
        choregraphieStepService.supprimerChoregraphieParId(1);

        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void testObtenirChoregraphieParId_success() {
        when(repository.findById(1)).thenReturn(Optional.of(choregraphie));

        ChoregraphieDeStep result = choregraphieStepService.obtenirChoregaphieParId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdCours());
        verify(repository).findById(1);
    }

    @Test
    void testObtenirChoregraphieParId_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ChoregraphieNonTrouveException.class,
                () -> choregraphieStepService.obtenirChoregaphieParId(99));

        verify(repository).findById(99);
    }

    @Test
    void testFindByUtilisateurIdUtilisateur_success() {
        when(repository.findByUtilisateurIdUtilisateur(5)).thenReturn(List.of(choregraphie));

        List<ChoregraphieDeStep> result = choregraphieStepService.findByUtilisateurIdUtilisateur(5);

        assertEquals(1, result.size());
        assertEquals("Test chorégraphie", result.get(0).getTitre());
        verify(repository).findByUtilisateurIdUtilisateur(5);
    }

    @Test
    void testObtenirListeChoreghraphieStep_returnsEmptyList() {
        List<ChoregraphieDeStepDTO> result = choregraphieStepService.obtenirListeChoreghraphieStep();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}