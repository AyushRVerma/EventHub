package com.eventbook.EventHub.Config;

import com.eventbook.EventHub.filters.RateLimiterFilter;
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
            HttpSecurity http, 
            UserProvisioningFilter userProvisioningFilter, 
            JwtAuthenticationConverter jwtAuthenticationConverter, 
            RateLimiterFilter rateLimiterFilter,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorize ->
                                //Catch all rule
                                authorize
                                        .requestMatchers("/swagger-ui/**" , "/v3/api-docs/**").permitAll()
                                        .requestMatchers("/api/v1/webhooks/razorpay").permitAll()
                                        .requestMatchers("/api/v1/auth/register", "/api/v1/config/**", "/api/v1/tickets/send-confirmation-email", "/api/v1/organizer/analytics").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/published-events", "/api/v1/published-events/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/events/*/photos", "/api/v1/events/photos/*").permitAll()
                                        .requestMatchers("/api/v1/events").hasRole("ORGANIZER")
                                        .requestMatchers("/api/v1/organizer/**").hasAnyRole("ORGANIZER", "ADMIN")
                                        .requestMatchers("/api/v1/ticket-validations/**")
                                        .hasAnyRole("STAFF", "ORGANIZER", "ADMIN")
                                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/ticket-types/*/tickets").hasRole("ATTENDEE")
                                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 ->
                        oauth2.successHandler(oAuth2AuthenticationSuccessHandler))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
//                                Customizer.withDefaults()
                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        ))
                .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class)
                .addFilterAfter(rateLimiterFilter,UserProvisioningFilter.class);

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow any localhost origin dynamically (5173, 5174, 5175, etc.)
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    
}
