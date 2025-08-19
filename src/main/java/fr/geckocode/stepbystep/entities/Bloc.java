package fr.geckocode.stepbystep.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bloc")

public class Bloc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_bloc", nullable = false)
    private Byte idBloc;

    @Column(name = "nombre_de_temps")
    private Byte nombreDeTemps;

    @Column(name = "ordre")
    private Byte ordre;

    @Column(name = "bloc_symmetrique")
    private Boolean bloc_symetrique;

    @ManyToOne
    @JoinColumn(name = "id_cours")
    @JsonBackReference
    private ChoregraphieDeStep choregraphieDeStep;

    @OneToMany(mappedBy = "bloc", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Mouvement> listeMouvements = new ArrayList<>();

}
