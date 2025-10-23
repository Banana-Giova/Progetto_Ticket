package it.degroup.it_tickets.config;
import org.springframework.context.annotation.Bean;
import it.degroup.it_tickets.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(c -> c.disable())
                .authorizeHttpRequests(r ->
                       r.requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/new_email_token").permitAll()
                        .requestMatchers("/email-confirmation").permitAll()
                        .requestMatchers("/reset-password").permitAll()
                        .requestMatchers("/forgot-password").permitAll()
                        .requestMatchers("/categories").permitAll()

                               .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/dev/**").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/tickets").authenticated()
                        .requestMatchers("/tickets/*").authenticated()
                        .requestMatchers("/profile-fetch").authenticated()
                        .requestMatchers("/get-users-list").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}