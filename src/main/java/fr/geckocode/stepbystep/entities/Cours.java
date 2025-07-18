package fr.geckocode.stepbystep.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "cours")
@NoArgsConstructor
@AllArgsConstructor
/*
/Relation d'heritage strategie JOINED pour une relation la plus representative possible de la base de donnees
/peu de jointures donc baisse des performances minimales
*/
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cours",nullable = false)
    private Integer idCours;

    @Column(name = "titre", length = 50)
    private String titre;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

}