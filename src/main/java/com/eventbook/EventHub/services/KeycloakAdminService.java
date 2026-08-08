package com.eventbook.EventHub.services;

import com.eventbook.EventHub.domain.DTOs.RegisterRequestDto;
import com.eventbook.EventHub.domain.entity.AuthProvider;
import com.eventbook.EventHub.domain.entity.Role;
import com.eventbook.EventHub.domain.entity.User;
import com.eventbook.EventHub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.server-url:http://localhost:9090}")
    private String serverUrl;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    @Value("${keycloak.realm:EVENT-TICKET-PLATFORM}")
    private String targetRealm;

    public User registerUser(RegisterRequestDto dto) {
        // Prevent registering as ADMIN directly via public registration
        Role selectedRole = dto.getRole();
        if (selectedRole == Role.ROLE_ADMIN) {
            selectedRole = Role.ROLE_ATTENDEE;
        }

        // 1. Obtain Admin Access Token from Keycloak
        String adminToken = getAdminAccessToken();

        // 2. Create User in Keycloak target realm
        String userId = createUserInKeycloak(adminToken, dto);

        // 3. Set User Password in Keycloak
        setUserPasswordInKeycloak(adminToken, userId, dto.getPassword());

        // 4. Save/Provision User in local PostgreSQL Database
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setRole(selectedRole);
            user.setProviderId(userId);
            return userRepository.save(user);
        }

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setRole(selectedRole);
        newUser.setAuthProvider(AuthProvider.KEYCLOAK);
        newUser.setProviderId(userId);

        log.info("Registered new user {} with role {} via Keycloak Admin API", dto.getEmail(), selectedRole);
        return userRepository.save(newUser);
    }

    private String getAdminAccessToken() {
        try {
            String tokenUrl = serverUrl + "/realms/master/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "password");
            map.add("client_id", "admin-cli");
            map.add("username", adminUsername);
            map.add("password", adminPassword);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.error("Failed to obtain Keycloak admin access token: {}", e.getMessage());
        }
        return null;
    }

    private String createUserInKeycloak(String adminToken, RegisterRequestDto dto) {
        String usersUrl = serverUrl + "/admin/realms/" + targetRealm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (adminToken != null) {
            headers.setBearerAuth(adminToken);
        }

        JSONObject userJson = new JSONObject();
        userJson.put("username", dto.getEmail());
        userJson.put("email", dto.getEmail());
        userJson.put("firstName", dto.getName());
        userJson.put("enabled", true);
        userJson.put("emailVerified", true);

        HttpEntity<String> request = new HttpEntity<>(userJson.toString(), headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(usersUrl, request, String.class);

            // Get Keycloak User ID from Location header or query by email
            if (response.getHeaders().getLocation() != null) {
                String path = response.getHeaders().getLocation().getPath();
                return path.substring(path.lastIndexOf('/') + 1);
            }
        } catch (Exception e) {
            log.warn("Keycloak user creation warning: {}", e.getMessage());
        }

        // Fallback: Query Keycloak for user ID by email
        return getUserIdByEmail(adminToken, dto.getEmail());
    }

    private void setUserPasswordInKeycloak(String adminToken, String userId, String password) {
        if (userId == null) return;
        String passwordUrl = serverUrl + "/admin/realms/" + targetRealm + "/users/" + userId + "/reset-password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (adminToken != null) {
            headers.setBearerAuth(adminToken);
        }

        JSONObject credentialJson = new JSONObject();
        credentialJson.put("type", "password");
        credentialJson.put("value", password);
        credentialJson.put("temporary", false);

        HttpEntity<String> request = new HttpEntity<>(credentialJson.toString(), headers);
        try {
            restTemplate.put(passwordUrl, request);
        } catch (Exception e) {
            log.error("Failed to set user password in Keycloak: {}", e.getMessage());
        }
    }

    private String getUserIdByEmail(String adminToken, String email) {
        try {
            String searchUrl = serverUrl + "/admin/realms/" + targetRealm + "/users?email=" + email;
            HttpHeaders headers = new HttpHeaders();
            if (adminToken != null) {
                headers.setBearerAuth(adminToken);
            }
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONArray users = new JSONArray(response.getBody());
                if (!users.isEmpty()) {
                    return users.getJSONObject(0).getString("id");
                }
            }
        } catch (Exception e) {
            log.error("Failed to query Keycloak user by email: {}", e.getMessage());
        }
        return null;
    }
}
