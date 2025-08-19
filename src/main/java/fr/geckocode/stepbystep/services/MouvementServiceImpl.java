package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Mouvement;
import fr.geckocode.stepbystep.repositories.MouvementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MouvementServiceImpl implements IMouvementService{

    private final MouvementRepository mouvementRepository;

    @Override
    public Mouvement findMouvementByNom(String nom) {
        return mouvementRepository.findByNom(nom);
    }

    @Override
    public List<Mouvement> rechercherParNom(String nom) {
        return mouvementRepository.findByNomContainingIgnoreCase(nom);
    }

}
