package fr.geckocode.stepbystep.services;

import fr.geckocode.stepbystep.entities.Bloc;
import fr.geckocode.stepbystep.entities.dto.BlocDto;
import java.util.Optional;

public interface IBlocService {

    Bloc creationBloc(BlocDto bloc);

    Optional<Bloc> getBlocById(Byte id);

}
