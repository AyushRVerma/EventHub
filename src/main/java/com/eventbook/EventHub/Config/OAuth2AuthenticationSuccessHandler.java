package com.eventbook.EventHub.Config;

import com.eventbook.EventHub.domain.entity.AuthProvider;
import com.eventbook.EventHub.domain.entity.Role;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final CustomJwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (name == null) {
            name = oAuth2User.getAttribute("login");
        }
        
        String providerId = oAuth2User.getName();
        AuthProvider authProvider = provider.equalsIgnoreCase("google") ? AuthProvider.GOOGLE : AuthProvider.GITHUB;

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getAuthProvider() != authProvider) {
                user.setAuthProvider(authProvider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            user = new User();
            user.setId(UUID.randomUUID());
            user.setEmail(email);
            user.setName(name != null ? name : "Unknown");
            user.setAuthProvider(authProvider);
            user.setProviderId(providerId);
            
            // Assign default role (e.g. ROLE_ATTENDEE)
            user.setRole(Role.ROLE_ATTENDEE);
            userRepository.save(user);
        }

        // Generate custom JWT token containing the user's role
        String jwtToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getName(), user.getRole());

        // Determine redirect origin dynamically (Vite defaults to 5173/5174/5175)
        String referer = request.getHeader("Referer");
        String frontendUrl = "http://localhost:5175";
        if (referer != null && referer.contains("localhost")) {
            if (referer.contains(":5173")) frontendUrl = "http://localhost:5173";
            else if (referer.contains(":5174")) frontendUrl = "http://localhost:5174";
            else if (referer.contains(":5175")) frontendUrl = "http://localhost:5175";
            else if (referer.contains(":3000")) frontendUrl = "http://localhost:3000";
        }

        String targetUrl = frontendUrl + "/oauth2/redirect?token=" + jwtToken + "&email=" + user.getEmail() + "&name=" + user.getName() + "&role=" + user.getRole();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
