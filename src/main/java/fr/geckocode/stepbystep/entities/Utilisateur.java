package fr.geckocode.stepbystep.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name ="utilisateur")
public class Utilisateur implements Serializable, UserDetails{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id_utilisateur", nullable = false)
    private Integer idUtilisateur;

    @Column(name = "nom" ,length = 50, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 50, nullable = false)
    private String prenom;

    @Column(name = "mot_de_passe", length = 100, nullable = false)
    private String motDePasse;

    @Column(name = "email", length = 320, nullable = false)
    private String email;

    //La gestion des cours est gerée par l'Entité Utilisateur ( ex:suppression d’un utilisateur supprime aussi ses cours )
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours = new ArrayList<>();

    //Chargement EAGER (par Defaut LAZY) = on veut que les roles soit chargés directement lorsque l utilisateur est identifié
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getNomRole().toString())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
