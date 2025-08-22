package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Bloc;
import fr.geckocode.stepbystep.entities.ChoregraphieDeStep;
import fr.geckocode.stepbystep.entities.Mouvement;
import fr.geckocode.stepbystep.entities.Utilisateur;
import fr.geckocode.stepbystep.entities.dto.BlocDto;
import fr.geckocode.stepbystep.mappers.MapperTool;
import fr.geckocode.stepbystep.repositories.BlocRepository;
import fr.geckocode.stepbystep.repositories.ChoregraphieStepRepository;
import fr.geckocode.stepbystep.repositories.MouvementRepository;
import fr.geckocode.stepbystep.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlocServiceImpl implements IBlocService{

    private final BlocRepository blocRepository;
    private final MouvementRepository mouvementRepository;
    private final MapperTool mapper;
    private final ChoregraphieStepServiceImpl choregraphieStepService;
    private final ChoregraphieStepRepository choregraphieRepository;
    private final UtilisateurRepository utilisateurRepository;


    @Transactional
    @Override
    public Bloc creationBloc(BlocDto dto) {
        // 1. Récupérer l'utilisateur persistant depuis la base
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getChoregraphieDeStep().getUtilisateur().getIdUtilisateur())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2. Construire et remplir l'entité ChoregraphieDeStep depuis le DTO
        ChoregraphieDeStep choregraphie = new ChoregraphieDeStep();

        choregraphie.setNomScript(dto.getChoregraphieDeStep().getNomScript());
        choregraphie.setNombreTotalMouvement(dto.getChoregraphieDeStep().getNombreTotalMouvement());
        choregraphie.setNiveau(dto.getChoregraphieDeStep().getNiveau());
        choregraphie.setDureeTotale(dto.getChoregraphieDeStep().getDureeTotale());
        choregraphie.setDateCreation(dto.getChoregraphieDeStep().getDateCreation());
        choregraphie.setTitre(dto.getChoregraphieDeStep().getTitre());
        choregraphie.setBpm(dto.getChoregraphieDeStep().getBpm());


        // 3. Associer l'utilisateur persistant à la chorégraphie
        choregraphie.setUtilisateur(utilisateur);

        utilisateur.getCours().add(choregraphie);

        // 4. Sauvegarder la chorégraphie pour générer idCours auto-incrementé
        choregraphie = choregraphieRepository.save(choregraphie);


        // 5. Créer le Bloc et lier la chorégraphie persistée
        Bloc bloc = new Bloc();
        bloc.setNombreDeTemps(dto.getNombreDeTemps());
        bloc.setOrdre(dto.getOrdre());
        bloc.setBloc_symetrique(dto.getBloc_symetrique());
        bloc.setChoregraphieDeStep(choregraphie);

        System.out.println(choregraphie);

        // 6. Gérer la liste des mouvements liés
        if (dto.getListeMouvements() != null) {
            for (var mouvDto : dto.getListeMouvements()) {
                Mouvement mouvEnBase = mouvementRepository.findById(mouvDto.getIdMouvement())
                        .orElseThrow(() -> new RuntimeException("Mouvement introuvable : " + mouvDto.getIdMouvement()));

                mouvEnBase.setBloc(bloc);
                bloc.getListeMouvements().add(mouvEnBase);
            }
        }

        // 7. Sauvegarder le bloc (avec cascade)
        return blocRepository.save(bloc);
    }

//    @Transactional
//    @Override
//    public Bloc creationBloc(BlocDto dto) {
//        Bloc bloc = new Bloc();
//        bloc.setNombreDeTemps(dto.getNombreDeTemps());
//        bloc.setOrdre(dto.getOrdre());
//        bloc.setBloc_symetrique(dto.getBloc_symetrique());
//        bloc.setChoregraphieDeStep(dto.getChoregraphieDeStep());
//
//
//
//
//        if(dto.getListeMouvements() != null){
//            for(var mouvDto : dto.getListeMouvements()){
//                Mouvement mouvementEnBase = mouvementRepository.findById(mouvDto.getIdMouvement())
//                        .orElseThrow(() -> new RuntimeException("Mouvement introuvable : " + mouvDto.getIdMouvement()));
//
//                mouvementEnBase.setBloc(bloc); // <<--- mettre à jour la relation propriétaire!
//
//                bloc.getListeMouvements().add(mouvementEnBase); // garder la cohérence en mémoire
//            }
//
//
//        }
//
//        // Sauvegarde : grâce à cascade ALL, tout est persisté
//        return blocRepository.save(bloc);
//    }


    @Override
    public Optional<Bloc> getBlocById(Byte id) {
        Optional<Bloc> bloc = blocRepository.findById(id);
        bloc.ifPresent(b -> b.getListeMouvements().size()); // force la loading lazy
        return bloc;
    }
}
