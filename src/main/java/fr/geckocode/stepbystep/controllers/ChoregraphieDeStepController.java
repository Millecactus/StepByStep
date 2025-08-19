package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import fr.geckocode.stepbystep.services.ChoregraphieStepServiceImpl;
import fr.geckocode.stepbystep.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/choregraphieDeStep")
public class ChoregraphieDeStepController {

    private final ChoregraphieStepServiceImpl service;
    private final MapperTool mapperTool;
    private final JwtService jwtService;
    private final ChoregraphieStepRepository choregraphieStepRepository;
    private final UtilisateurRepository utilisateurRepository;


    @GetMapping(value = "/hello")
    public String hello(){
        return "Hello";
    }

    @PostMapping("/creation-choregraphie")
    public ResponseEntity<ChoregraphieDeStepDTO> create(@RequestBody ChoregraphieDeStepDTO dto) {
        ChoregraphieDeStep entity = mapperTool.convertirDtoEnEntite(dto, ChoregraphieDeStep.class); // convertir DTO vers entité
        ChoregraphieDeStep savedEntity = service.creationChoregraphieDeStep(entity);
        ChoregraphieDeStepDTO savedDto = mapperTool.convertirDtoEnEntite(savedEntity, ChoregraphieDeStepDTO.class); // convertir entité vers DTO
        return ResponseEntity.ok(savedDto);
    }

    @DeleteMapping (value ="/suppression/{id}")
    public void suppression(@PathVariable Integer id){
        service.supprimerChoregraphieParId(id);
    }


    @GetMapping (value ="/obtenirChoregraphieParId/{id}")
    public ResponseEntity<ChoregraphieDeStep> obtenirChoregraphieParId(@PathVariable Integer id){
        ChoregraphieDeStep choregraphieDeStep = service.obtenirChoregaphieParId(id);
        return ResponseEntity.ok(choregraphieDeStep);
    }


    @GetMapping("/obtenirChoregaphieUtilisateurConnecte")
    public List<ChoregraphieDeStep> getChoregraphieParidUtilisateur(Authentication authentication) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Integer idUtilisateurConnecte = utilisateur.getIdUtilisateur();
        return choregraphieStepRepository.findByUtilisateurIdUtilisateur(idUtilisateurConnecte);
    }


}
