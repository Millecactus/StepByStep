package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // On injecte manuellement la clé secrète (Base64) pour les tests
        jwtService.secretKey = Base64.getEncoder().encodeToString("ThisIsASecretKeyForJWTGeneration".getBytes());
    }

    @Test
    void testGenerateToken_and_extractUsername() {
        GrantedAuthority authority = () -> "ROLE_USER";

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(123);
        utilisateur.setEmail("testuser@example.com");

        // Ici on mock juste les autorités de l'utilisateur concret
        Utilisateur spyUser = spy(utilisateur);
        doReturn(List.of(authority)).when(spyUser).getAuthorities();

        String token = jwtService.generateToken(spyUser);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser@example.com", extractedUsername);
    }

    @Test
    void testIsTokenValid_valid_and_expired() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));

        JwtService shortLivedJwtService = new JwtService() {
            @Override
            public String generateToken(Map<String, Object> extraClaims, UserDetails userDetail) {
                return Jwts.builder()
                        .setSubject(userDetail.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                        .setExpiration(new Date(System.currentTimeMillis() - 5000))
                        .signWith(getSignInKey())
                        .compact();
            }
        };
        shortLivedJwtService.secretKey = jwtService.secretKey;

        String expiredToken = shortLivedJwtService.generateToken(Collections.emptyMap(), userDetails);
        assertFalse(shortLivedJwtService.isTokenValid(expiredToken, userDetails));
    }


    @Test
    void testExtractClaim() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
    }
}
