package fr.geckocode.stepbystep.entities;

import fr.geckocode.stepbystep.enums.NomRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table (name = "role")

public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name ="nom_role" , length = 20)
    private NomRole nomRole;

    public Role(NomRole nomRole) {
        this.nomRole = nomRole;
    }

    @ManyToMany(mappedBy = "roles")
    private Collection<Utilisateur> users;

}
