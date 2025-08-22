package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Bloc;
import fr.geckocode.stepbystep.entities.dto.BlocDto;
import fr.geckocode.stepbystep.services.BlocServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlocControllerTest {

    @Mock
    private BlocServiceImpl blocService;

    @InjectMocks
    private BlocController blocController;

    private Bloc blocMock;
    private BlocDto blocDtoMock;

    @BeforeEach
    void setUp() {
        blocDtoMock = new BlocDto();
        blocMock = new Bloc();
    }

    // Test pour la méthode sayHello()
    @Test
    void sayHello_ReturnsHello() {
        // Act
        String result = blocController.sayHello();

        // Assert
        assertEquals("Hello", result);
    }

    // Test pour la méthode creationBloc()
    @Test
    void creationBloc_ValidDto_ReturnsOk() {
        // Arrange
        when(blocService.creationBloc(any(BlocDto.class))).thenReturn(blocMock);

        // Act
        ResponseEntity<Bloc> response = blocController.creationBloc(blocDtoMock);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(blocMock, response.getBody());
        verify(blocService, times(1)).creationBloc(blocDtoMock);
    }

    // Test pour la méthode getBlockById() avec un ID existant
    @Test
    void getBlockById_ExistingId_ReturnsBloc() {
        // Arrange
        Byte id = 1;
        when(blocService.getBlocById(id)).thenReturn(Optional.of(blocMock));

        // Act
        Optional<Bloc> result = blocController.getBlockById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(blocMock, result.get());
        verify(blocService, times(1)).getBlocById(id);
    }

    // Test pour la méthode getBlockById() avec un ID non existant
    @Test
    void getBlockById_NonExistingId_ReturnsEmpty() {
        // Arrange
        Byte id = 1;
        when(blocService.getBlocById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Bloc> result = blocController.getBlockById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(blocService, times(1)).getBlocById(id);
    }
}
