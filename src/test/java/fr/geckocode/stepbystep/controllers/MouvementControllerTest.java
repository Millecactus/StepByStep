package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Mouvement;
import fr.geckocode.stepbystep.services.IMouvementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MouvementControllerTest {

    @Mock
    private IMouvementService mouvementService;

    @InjectMocks
    private MouvementController mouvementController;

    private List<Mouvement> mouvementsMock;

    @BeforeEach
    void setUp() {
        Mouvement mouvement1 = new Mouvement();
        mouvement1.setIdMouvement((byte) 1L);
        mouvement1.setNom("Step 1");

        Mouvement mouvement2 = new Mouvement();
        mouvement2.setIdMouvement((byte) 2L);
        mouvement2.setNom("Step 2");

        mouvementsMock = Arrays.asList(mouvement1, mouvement2);
    }

    @Test
    void chercherMouvements_QueryNull_ReturnsBadRequest() {
        // Arrange
        String query = null;

        // Act
        ResponseEntity<List<Mouvement>> response = mouvementController.chercherMouvements(query);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void chercherMouvements_QueryTooShort_ReturnsBadRequest() {
        // Arrange
        String query = "a";

        // Act
        ResponseEntity<List<Mouvement>> response = mouvementController.chercherMouvements(query);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void chercherMouvements_QueryValidButNoResults_ReturnsNoContent() {
        // Arrange
        String query = "step";
        when(mouvementService.rechercherParNom(query)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Mouvement>> response = mouvementController.chercherMouvements(query);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void chercherMouvements_QueryValidWithResults_ReturnsOk() {
        // Arrange
        String query = "step";
        when(mouvementService.rechercherParNom(query)).thenReturn(mouvementsMock);

        // Act
        ResponseEntity<List<Mouvement>> response = mouvementController.chercherMouvements(query);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mouvementsMock, response.getBody());
        verify(mouvementService, times(1)).rechercherParNom(query);
    }
}
