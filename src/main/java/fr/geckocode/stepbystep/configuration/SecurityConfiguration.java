package fr.geckocode.stepbystep.configuration;

import fr.geckocode.stepbystep.securite.JwtAuthFiltrer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFiltrer jwtFiltrer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/utilisateur/inscription").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/utilisateur/connexion").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/utilisateur/ajouter-roles").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/utilisateur/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/admin/**").permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFiltrer, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

}
