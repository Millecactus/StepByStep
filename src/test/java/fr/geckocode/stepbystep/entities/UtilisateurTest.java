package fr.geckocode.stepbystep.entities;

import fr.geckocode.stepbystep.enums.NomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Utilisateur utilisateur;
    private List<Role> roles;

    @BeforeEach
    void setUp() {
        Role roleUser = new Role();
        roleUser.setNomRole(NomRole.USER);

        Role roleAdmin = new Role();
        roleAdmin.setNomRole(NomRole.ADMIN);

        roles = List.of(roleUser, roleAdmin);

        utilisateur = Utilisateur.builder()
                .idUtilisateur(1)
                .nom("Doe")
                .prenom("John")
                .motDePasse("password123")
                .email("john.doe@example.com")
                .roles(roles)
                .build();
    }

    @Test
    void getAuthorities_ReturnsCorrectAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> authorities = utilisateur.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        // Vérifie les autorités SANS le préfixe "ROLE_"
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
    }


    @Test
    void getPassword_ReturnsCorrectPassword() {
        // Act
        String password = utilisateur.getPassword();

        // Assert
        assertEquals("password123", password);
    }

    @Test
    void getUsername_ReturnsCorrectEmail() {
        // Act
        String username = utilisateur.getUsername();

        // Assert
        assertEquals("john.doe@example.com", username);
    }

    @Test
    void isAccountNonExpired_ReturnsTrue() {
        // Act
        boolean isAccountNonExpired = utilisateur.isAccountNonExpired();

        // Assert
        assertTrue(isAccountNonExpired);
    }

    @Test
    void isAccountNonLocked_ReturnsTrue() {
        // Act
        boolean isAccountNonLocked = utilisateur.isAccountNonLocked();

        // Assert
        assertTrue(isAccountNonLocked);
    }

    @Test
    void isCredentialsNonExpired_ReturnsTrue() {
        // Act
        boolean isCredentialsNonExpired = utilisateur.isCredentialsNonExpired();

        // Assert
        assertTrue(isCredentialsNonExpired);
    }

    @Test
    void isEnabled_ReturnsTrue() {
        // Act
        boolean isEnabled = utilisateur.isEnabled();

        // Assert
        assertTrue(isEnabled);
    }

    @Test
    void getters_ReturnCorrectValues() {
        // Assert
        assertEquals(1, utilisateur.getIdUtilisateur());
        assertEquals("Doe", utilisateur.getNom());
        assertEquals("John", utilisateur.getPrenom());
        assertEquals("password123", utilisateur.getMotDePasse());
        assertEquals("john.doe@example.com", utilisateur.getEmail());
        assertEquals(roles, utilisateur.getRoles());
    }

    @Test
    void setters_UpdateValuesCorrectly() {
        // Act
        utilisateur.setNom("Smith");
        utilisateur.setPrenom("Alice");
        utilisateur.setMotDePasse("newpassword");
        utilisateur.setEmail("alice.smith@example.com");

        // Assert
        assertEquals("Smith", utilisateur.getNom());
        assertEquals("Alice", utilisateur.getPrenom());
        assertEquals("newpassword", utilisateur.getMotDePasse());
        assertEquals("alice.smith@example.com", utilisateur.getEmail());
    }
}
