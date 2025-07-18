package fr.geckocode.stepbystep.entities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
    private String motDePasse;
    private String email;
}
