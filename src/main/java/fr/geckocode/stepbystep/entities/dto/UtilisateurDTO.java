package fr.geckocode.stepbystep.entities.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    @NotNull(message = "Le nom est obligatoire")
    @Size (max = 50)
    private String nom;

    @NotNull(message = "Le prenom est obligatoire")
    @Size (max = 50)
    private String prenom;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 32, message = "Le mot de passe doit contenir entre 8 et 32 caractères")
    private String motDePasse;

    @Email(message = "L'adresse mail est obligatoire")
    @Size(max = 320)
    private String email;
}
