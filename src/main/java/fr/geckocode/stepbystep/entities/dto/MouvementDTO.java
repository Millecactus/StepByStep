package fr.geckocode.stepbystep.entities.dto;

import lombok.Data;

@Data
public class MouvementDTO {

    private Byte idMouvement;
    private Byte nombreDeRepetitions;
    private Boolean mouvementAlterne;
    private Byte temps;
    private String nom;
    private String description;
    private String variante;
    private String video;
//    private Bloc bloc;
}
