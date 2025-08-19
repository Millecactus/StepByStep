package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Mouvement;
import fr.geckocode.stepbystep.services.IMouvementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/mouvements")
@RequiredArgsConstructor
public class MouvementController {

    private final IMouvementService mouvementService;

    @GetMapping("/recherche")
    public ResponseEntity<List<Mouvement>> chercherMouvements(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) {
            // Mauvaise requête si le paramètre est absent ou trop court
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Mouvement> mouvements = mouvementService.rechercherParNom(query.trim());

        if (mouvements.isEmpty()) {
            // Pas de contenu trouvé0
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(mouvements);
    }
}