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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements IUtilisateurService, UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final ChoregraphieStepRepository choregraphieStepRepository;
    private final MapperTool mapperTool;
    private final RoleRepository roleRepository;

    @Override
    public Utilisateur obtenirUtilisateur(Integer id) {
        try {
            log.info("Recherche de l'utilisateur avec l'id : {}", id);
            return utilisateurRepository.findById(id)
                    .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur avec l'identifiant " + id + " non trouvé."));
        } catch (UtilisateurNonTrouveException e) {
            log.error("Erreur : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération de l'utilisateur : {}", id, e);
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur");
        }
    }

    @Override
    public void supprimerUtilisateur(Integer id) {
        try {
            log.info("Suppression de l'utilisateur avec l'id : {}", id);
            utilisateurRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'utilisateur avec l'id : {}", id, e);
            throw new RuntimeException("Impossible de supprimer l'utilisateur avec l'id : " + id);
        }
    }

    @Override
    public List<UtilisateurDTO> obtenirListeUtilisateur() {
        try {
            log.info("Récupération de la liste des utilisateurs");
            return utilisateurRepository.findAll()
                    .stream()
                    .map(utilisateur -> mapperTool.convertirEntiteEnDto(utilisateur, UtilisateurDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la liste des utilisateurs", e);
            throw new RuntimeException("Impossible de récupérer la liste des utilisateurs");
        }
    }




    @Override
    public Role ajouterRole(Role role) {
        try {
            log.info("Ajout du rôle : {}", role.getNomRole());
            return roleRepository.save(role);
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du rôle : {}", role.getNomRole(), e);
            throw new RuntimeException("Impossible d'ajouter le rôle : " + role.getNomRole());
        }
    }

    @Transactional //permet de traiter plusieurs actions de maniere atomique (soit tout passe, soit rien)
    @Override
    public void ajouterRoleUtilisateur(Utilisateur utilisateur, List<Role> listeRole) {
        try {
            log.info("Ajout de rôles à l'utilisateur : {}", utilisateur.getEmail());
            Utilisateur utilisateurEnBase = utilisateurRepository.findByEmail(utilisateur.getEmail())
                    .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur non trouvé"));

            for (Role role : listeRole) {
                Role roleEnBase = roleRepository.findByNomRole(role.getNomRole())
                        .orElseThrow(() -> new IllegalStateException("Role non trouvé : " + role.getNomRole()));
                utilisateurEnBase.getRoles().add(roleEnBase);
                log.info("Rôle ajouté : {}", role.getNomRole());
            }

            utilisateurRepository.save(utilisateurEnBase);
        } catch (UtilisateurNonTrouveException | IllegalStateException e) {
            log.error("Erreur : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout de rôles à l'utilisateur : {}", utilisateur.getEmail(), e);
            throw new RuntimeException("Impossible d'ajouter les rôles à l'utilisateur");
        }
    }

    @Override
    public Utilisateur obtenirParEmail(String email) {
        try {
            log.info("Recherche de l'utilisateur par email : {}", email);
            return utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new UtilisateurNonTrouveException("Utilisateur avec l'email " + email + " non trouvé."));
        } catch (UtilisateurNonTrouveException e) {
            log.error("Erreur : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération de l'utilisateur par email : {}", email, e);
            throw new RuntimeException("Impossible de récupérer l'utilisateur par email");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Chargement de l'utilisateur pour Spring Security : {}", email);
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
            return new org.springframework.security.core.userdetails.User(
                    utilisateur.getEmail(),
                    utilisateur.getMotDePasse(),
                    Collections.emptyList()
            );
        } catch (UsernameNotFoundException e) {
            log.error("Erreur : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors du chargement de l'utilisateur : {}", email, e);
            throw new RuntimeException("Impossible de charger l'utilisateur pour l'authentification");
        }
    }



}

