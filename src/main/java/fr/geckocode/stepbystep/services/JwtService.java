package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    //recupere la variable declacrée dans le application.properties
    @Value("${spring.jwt.security.key}")
    public String secretKey;

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Génère un JWT personnalisé en fonction des claims et des informations utilisateur.
     *
     * @param extraClaims    Claims supplémentaires à inclure dans le token (par exemple email, nom, etc.)
     * @param userDetail     Les informations de l'utilisateur authentifié, nécessaires pour le sujet et les rôles.
     * @return               La chaîne du JWT signé et prêt à être transmis au client.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetail) {
        // On prépare la map où seront stockées toutes les données à inclure dans le JWT (claims)
        Map<String, Object> claims = new HashMap<>();

        // On récupère la liste des rôles de l'utilisateur pour l'ajouter comme claim
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : userDetail.getAuthorities()) {
            System.out.println("Authority: " + authority.getAuthority());
            roles.add(authority.getAuthority()); // Ajoute chaque rôle/autorité à la liste
        }
        // Ajoute la liste des rôles (ou autorités) dans les claims sous la clé "roles"
        claims.put("roles", roles);


        // Ajout de l'ID utilisateur
        // ⚠️ Ici il faut accéder à ton implémentation personnalisée de UserDetails
        // pour récupérer l'id en base
        if (userDetail instanceof Utilisateur utilisateur) {
            claims.put("idUtilisateur", utilisateur.getIdUtilisateur());

        }


        // Ajoute tous les claims supplémentaires passés en paramètre à la map
        claims.putAll(extraClaims);

        // Utilise le builder JWT pour créer le token :
        // - Ajoute les claims construits ci-dessus
        // - Définit le "subject" (sujet du token), ici le nom d'utilisateur
        // - Définit la date d'émission
        // - Définit la date d'expiration (valable ici 1 heure après l'émission)
        // - Signe le token avec la clé secrète (HMAC)
        // - Appelle compact() pour générer la version finale encodée du JWT
        return Jwts.builder()
                .claims(claims)
                .subject(userDetail.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 ))
                .signWith(getSignInKey()) // Utilise la clé générée précédemment pour signer
                .compact(); // Génère la chaîne finale du token
    }

    /**
     * Récupère la clé secrète à utiliser pour la signature des JWT.
     * La clé est attendue au format Base64 dans la variable secretKey.
     * Elle est décodée puis convertie en objet Key compatible HMAC-SHA.
     *
     * @return Key utilisée pour signer et vérifier les tokens JWT.
     */
    public SecretKey getSignInKey() {
        // Décodage Base64 de la clé secrète afin d’obtenir le tableau d’octets original
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // Génération (ou empaquetage) de la clé dans un format utilisable
        // par les algorithmes de signature HMAC (SHA-256, SHA-512, etc.)
        // Permet à la librairie JWT de l’utiliser directement pour signer ou vérifier les tokens
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // méthode générique permettant d extraire n'importe quelle info passée en parametres
    public <T> T extractClaim (String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt,Claims::getSubject);
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt,Claims::getExpiration);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails){
        try {
            final String username = extractUsername(jwt);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        } catch (ExpiredJwtException e) {
            return false; // Token expiré => non valide
        }
/*        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));*/
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }
}
