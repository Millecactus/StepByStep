package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Mouvement;
import java.util.List;

public interface IMouvementService {

    Mouvement findMouvementByNom(String nom);

    List<Mouvement> rechercherParNom(String nom);



}
