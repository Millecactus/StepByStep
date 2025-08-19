package fr.geckocode.stepbystep.controllers;

import fr.geckocode.stepbystep.entities.Bloc;
import fr.geckocode.stepbystep.entities.dto.BlocDto;
import fr.geckocode.stepbystep.services.BlocServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bloc")
@RequiredArgsConstructor
public class BlocController {

    private final BlocServiceImpl blocService;

    @PostMapping("/creation-bloc")
    public  ResponseEntity<Bloc> creationBloc(@RequestBody BlocDto dto){
        Bloc bloc = blocService.creationBloc(dto);
        return ResponseEntity.ok(bloc);
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

    @GetMapping("/bloc/{id}")
    public Optional<Bloc> getBlockById(@PathVariable Byte id){
        return blocService.getBlocById(id);
    }

}
