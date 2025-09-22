package fr.geckocode.stepbystep.repositories;

import fr.geckocode.stepbystep.entities.Role;
import fr.geckocode.stepbystep.enums.NomRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByNomRole(NomRole nomRole);
}
