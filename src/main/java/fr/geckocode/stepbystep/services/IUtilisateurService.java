package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import java.util.List;

public interface IUtilisateurService {

     Utilisateur obtenirUtilisateur(Integer id);

     void supprimerUtilisateur (Integer id);

     List<UtilisateurDTO> obtenirListeUtilisateur();


     Role ajouterRole(Role role);

     void ajouterRoleUtilisateur(Utilisateur utilisateur, List<Role> listeRole);

    Utilisateur obtenirParEmail(String email);
}

