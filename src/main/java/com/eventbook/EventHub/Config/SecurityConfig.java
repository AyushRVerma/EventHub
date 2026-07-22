package com.eventbook.EventHub.Config;

import com.eventbook.EventHub.filters.UserProvisioningFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, UserProvisioningFilter userProvisioningFilter , JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorize ->
                                //Catch all rule
                                authorize
                                        .requestMatchers("/swagger-ui/**" , "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/published-events/**")
                                        .permitAll()
                                        .requestMatchers("/api/v1/events").hasRole("ORGANIZER")
                                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/ticket-types/*/tickets").hasRole("ATTENDEE")
                                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
//                                Customizer.withDefaults()
                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        ))
                .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Add your frontend URLs here (Vite defaults to 5173/5174, React defaults to 3000)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
