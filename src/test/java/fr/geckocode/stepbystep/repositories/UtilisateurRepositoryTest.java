package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'interface {@link UtilisateurRepository}.
 *
 * Ces tests valident les méthodes personnalisées
 * findByEmail et findByNom via une base de données H2 en mémoire.
 *
 */
@DataJpaTest
public class UtilisateurRepositoryTest {

    /** Repository injecté pour les opérations sur les utilisateurs. */
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private Utilisateur utilisateur;

    /**
     * Initialise un utilisateur en base avant chaque test.
     */
    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("John");
        utilisateur.setEmail("john.doe@example.com");
        utilisateur.setMotDePasse("password");
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Vérifie que findByEmail retourne l'utilisateur attendu quand l'email existe.
     */
    @Test
    void testFindByEmail_found() {
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail("john.doe@example.com");
        assertTrue(resultat.isPresent());
        assertEquals("Doe", resultat.get().getNom());
    }

    /**
     * Vérifie que findByEmail retourne vide si l'email n'existe pas.
     */
    @Test
    void testFindByEmail_notFound() {
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail("unknown@email.com");
        assertTrue(resultat.isEmpty());
    }

    /**
     * Teste que findByNom retourne l'utilisateur attendu si le nom existe.
     */
    @Test
    void testFindByNom_found() {
        Utilisateur resultat = utilisateurRepository.findByNom("Doe");
        assertNotNull(resultat);
        assertEquals("john.doe@example.com", resultat.getEmail());
    }

    /**
     * Vérifie que findByNom retourne null si le nom n'existe pas.
     */
    @Test
    void testFindByNom_notFound() {
        Utilisateur resultat = utilisateurRepository.findByNom("NonExistant");
        assertNull(resultat);
    }
}
