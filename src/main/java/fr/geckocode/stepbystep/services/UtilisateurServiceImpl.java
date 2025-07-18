package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.UtilisateurDTO;
import fr.geckocode.stepbystep.exceptions.UtilisateurNonTrouveException;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.RoleRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import fr.geckocode.stepbystep.services.IUtilisateurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements IUtilisateurService, UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final ChoregraphieStepRepository choregraphieStepRepository;
    private final MapperTool mapperTool;
    private final RoleRepository roleRepository;

    @Override
    public Utilisateur obtenirUtilisateur(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur avec l'identifiant " + id + " non trouvé."));
    }

    @Override
    public void supprimerUtilisateur(Integer id) {
        utilisateurRepository.deleteById(id);
    }

    @Override
    public List<UtilisateurDTO> obtenirListeUtilisateur() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateur -> mapperTool.convertirEntiteEnDto(utilisateur, UtilisateurDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChoregraphieDeStep> obtenirListeChoregraphieParNomUtilisateur(String nomUtilisateur) {
        return choregraphieStepRepository.findByUtilisateurNom(nomUtilisateur);
    }

    @Override
    public Role ajouterRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional //permet de traiter plusieurs actions de maniere atomique (soit tout passe, soit rien)
    @Override
    public void ajouterRoleUtilisateur(Utilisateur utilisateur, List<Role> listeRole) {
        Utilisateur utilisateurEnBase = utilisateurRepository.findByEmail(utilisateur.getEmail())
                .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur non trouvé"));

        for (Role role : listeRole) {
            Role roleEnBase = roleRepository.findByNomRole(role.getNomRole());
            utilisateurEnBase.getRoles().add(roleEnBase);
        }

        utilisateurRepository.save(utilisateurEnBase);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                Collections.emptyList()
        );
    }
}

