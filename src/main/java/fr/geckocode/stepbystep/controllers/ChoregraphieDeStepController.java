package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.dto.ChoregraphieDeStepDTO;
import fr.geckocode.stepbystep.services.ChoregraphieStepServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/choregraphieDeStep")
public class ChoregraphieDeStepController {

    private final ChoregraphieStepServiceImpl service;

    @GetMapping(value = "/hello")
    public String hello(){
        return "Hello";
    }

    @PostMapping(value = "/creation")
    public ResponseEntity<ChoregraphieDeStep> creation(@RequestBody ChoregraphieDeStepDTO dto){
        ChoregraphieDeStep choregraphieDeStep = service.creationChoregraphieDeStep(dto);
        return ResponseEntity.ok(choregraphieDeStep);
    }

    @PostMapping (value ="/suppression/{id}")
    public void suppression(@PathVariable Integer id){
        service.supprimerChoregraphieParId(id);
    }


    @GetMapping (value ="/obtenirChoregraphieParId/{id}")
    public ResponseEntity<ChoregraphieDeStep> obtenirChoregraphieParId(@PathVariable Integer id){
        ChoregraphieDeStep choregraphieDeStep = service.obtenirChoregaphieParId(id);
        return ResponseEntity.ok(choregraphieDeStep);
    }
}
