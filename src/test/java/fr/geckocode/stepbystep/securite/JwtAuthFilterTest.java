package fr.geckocode.stepbystep.securite;

import fr.geckocode.stepbystep.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFiltrerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFiltrer jwtAuthFiltrer;

    private UserDetails userDetails;
    private String jwtToken;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        jwtToken = "valid.jwt.token";
        userDetails = new User(email, "", Collections.emptyList());
        // Réinitialiser le contexte de sécurité avant chaque test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Réinitialiser le contexte de sécurité après chaque test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFiltrer.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_InvalidAuthHeader_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Act
        jwtAuthFiltrer.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidTokenButAlreadyAuthenticated_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(email);
        // Initialiser une authentification existante
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // Act
        jwtAuthFiltrer.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername(jwtToken);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_ValidTokenAndNotAuthenticated_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);

        // Act
        jwtAuthFiltrer.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername(jwtToken);
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isTokenValid(jwtToken, userDetails);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_ValidTokenButInvalidUser_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(false);

        // Act
        jwtAuthFiltrer.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername(jwtToken);
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isTokenValid(jwtToken, userDetails);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
