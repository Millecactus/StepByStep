package fr.geckocode.stepbystep.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "choregraphie_de_step")
@PrimaryKeyJoinColumn(name = "id_cours")
public class ChoregraphieDeStep extends Cours implements Serializable {

    private static final long serialVersionUID = 1L;

    // L'ID est déjà défini dans la classe parente

    @Column(name = "nom_script", length = 50)
    private String nomScript;

    @Column(name = "nombre_total_mouvement")
    private Short nombreTotalMouvement; //short = SmallInt

    @Column (name = "script_symetrique")
    private boolean scriptSymetrique;

    @Column(name = "niveau", length = 50)
    private String niveau;

    @Column(name = "duree_totale")
    private Short dureeTotale;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @OneToMany(mappedBy = "choregraphieDeStep",cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<Bloc> listeBlocs= new ArrayList<>();

    @Column(name = "BPM")
    private Integer bpm;

}
