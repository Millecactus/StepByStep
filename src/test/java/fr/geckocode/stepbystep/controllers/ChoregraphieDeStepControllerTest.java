package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import fr.geckocode.stepbystep.services.ChoregraphieStepServiceImpl;
import fr.geckocode.stepbystep.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChoregraphieDeStepControllerTest {

    @Mock
    private ChoregraphieStepServiceImpl service;

    @Mock
    private MapperTool mapperTool;

    @Mock
    private JwtService jwtService;

    @Mock
    private ChoregraphieStepRepository choregraphieStepRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private ChoregraphieDeStepController controller;

    private ChoregraphieDeStepDTO dto;
    private ChoregraphieDeStep entity;

    @BeforeEach
    void setup() {
        dto = new ChoregraphieDeStepDTO();
        entity = new ChoregraphieDeStep();
    }

    @Test
    void testHello() {
        assertEquals("Hello", controller.hello());
    }

    @Test
    void testCreate() {

        when(mapperTool.convertirDtoEnEntite(any(ChoregraphieDeStepDTO.class), eq(ChoregraphieDeStep.class))).thenReturn(entity);
        when(service.creationChoregraphieDeStep(any(ChoregraphieDeStep.class))).thenReturn(entity);
        when(mapperTool.convertirDtoEnEntite(any(ChoregraphieDeStep.class), eq(ChoregraphieDeStepDTO.class))).thenReturn(dto);

        ResponseEntity<ChoregraphieDeStepDTO> response = controller.create(dto);

        assertNotNull(response);
        assertEquals(dto, response.getBody());

        verify(mapperTool).convertirDtoEnEntite(any(ChoregraphieDeStepDTO.class), eq(ChoregraphieDeStep.class));
        verify(service).creationChoregraphieDeStep(any(ChoregraphieDeStep.class));
        verify(mapperTool).convertirDtoEnEntite(any(ChoregraphieDeStep.class), eq(ChoregraphieDeStepDTO.class));
    }

    @Test
    void testSuppression() {
        doNothing().when(service).supprimerChoregraphieParId(anyInt());

        controller.suppression(1);

        verify(service, times(1)).supprimerChoregraphieParId(1);
    }

    @Test
    void testObtenirChoregraphieParId() {
        when(service.obtenirChoregaphieParId(1)).thenReturn(entity);

        ResponseEntity<ChoregraphieDeStep> response = controller.obtenirChoregraphieParId(1);

        assertNotNull(response);
        assertEquals(entity, response.getBody());

        verify(service, times(1)).obtenirChoregaphieParId(1);
    }


    @Test
    void testGetChoregraphieParidUtilisateur() {
        Authentication authentication = mock(Authentication.class);
        Utilisateur utilisateur = mock(Utilisateur.class);

        when(authentication.getPrincipal()).thenReturn(utilisateur);
        when(utilisateur.getIdUtilisateur()).thenReturn(1);
        when(choregraphieStepRepository.findByUtilisateurIdUtilisateur(1)).thenReturn(List.of(entity));

        List<ChoregraphieDeStep> result = controller.getChoregraphieParidUtilisateur(authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));

        verify(authentication).getPrincipal();
        verify(utilisateur).getIdUtilisateur();
        verify(choregraphieStepRepository).findByUtilisateurIdUtilisateur(1);
    }

}

