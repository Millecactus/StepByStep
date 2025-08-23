package fr.geckocode.stepbystep.integration;

import fr.geckocode.stepbystep.entities.dto.LoginRequestDTO;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class UtilisateurControllerIntegrationTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // Test de l'endpoint /api/v1/utilisateur/hello
    @Test
    public void testSayHello() {
        String url = "http://localhost:" + port + "/api/v1/utilisateur/hello";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello world");
    }

/**
 * Test de l'endpoint de connexion utilisateur via POST /connexion.
 *Ce test vérifie que la connexion avec des identifiants valides
 * retourne un code HTTP 200 (OK).
 *La requête est envoyée avec un corps JSON contenant l'email et
 * le mot de passe, ainsi qu'un header Content-Type application/json.
 * @throws Exception en cas d'erreur lors de l'exécution du test
 */
    @Test
    public void testConnexion() {
        String url = "http://localhost:" + port + "/api/v1/utilisateur/connexion";

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("angelique@email.com");
        loginRequest.setMotDePasse("angelique");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}

