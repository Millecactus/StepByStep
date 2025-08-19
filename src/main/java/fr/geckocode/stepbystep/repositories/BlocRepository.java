package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.Bloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlocRepository extends JpaRepository <Bloc, Byte> {


}
