package fr.geckocode.stepbystep.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mouvement")

public class Mouvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement", nullable = false)
    private Byte idMouvement;

    @Column(name = "nombre_de_repetitions")
    private Byte nombreDeRepetitions;

    @Column(name = "mouvement_alterne")
    private Boolean mouvementAlterne;

    @Column(name = "temps", nullable = false)
    private Byte temps;

    @Column(name = "nom", length = 150, nullable = false)
    private String nom;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "variante", length = 150)
    private String variante;

    @Column(name = "video", length = 255)
    private String video;

    @ManyToOne
    @JoinColumn(name = "id_bloc")
    private Bloc bloc;

}
