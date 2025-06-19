package org.phong.horizon.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Auth0UserService {

    private final RestTemplate restTemplate;

    @Value("${auth0.m2m-domain}")
    private String domain;

    @Value("${auth0.m2m-client-id}")
    private String clientId;

    @Value("${auth0.m2m-client-secret}")
    private String clientSecret;

    @Value("${auth0.m2m-audience}")
    private String audience;

    @Value("${auth0.m2m-api-identifier}")
    private String apiIdentifier;

    public void createUserAndSyncToAuth0(User localUser) {
        String token = getAccessToken();
        String userId = createAuth0User(localUser, token);

        String localRole = localUser.getRole();
        String roleId = getRoleId(localRole, token);

        if (roleId != null) {
            assignRole(userId, token, roleId);
        }
    }

    public List<Map<String, Object>> listRolesFromAuth0() {
        String token = getAccessToken();
        String url = domain + "/api/v2/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        return response.getBody();
    }

    public List<Map<String, Object>> getPermissionsForRole(String roleId) {
        String token = getAccessToken();
        String url = domain + "/api/v2/roles/" + roleId + "/permissions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        return response.getBody();
    }

    private String getAccessToken() {
        String url = domain + "/oauth/token";

        Map<String, String> requestBody = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "audience", audience,
                "grant_type", "client_credentials"
        );

        ResponseEntity<Auth0TokenResponse> response = restTemplate.postForEntity(url, requestBody, Auth0TokenResponse.class);
        assert response.getBody() != null;
        return response.getBody().getAccessToken();
    }

    private String createAuth0User(User user, String token) {
        String url = domain + "/api/v2/users";

        Map<String, Object> body = Map.of(
                "email", user.getEmail(),
                "password", user.getPassword(),
                "connection", "Username-Password-Authentication"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });
        return Objects.requireNonNull(response.getBody()).get("user_id").toString();
    }

    private void assignRole(String userId, String token, String roleId) {
        String url = domain + "/api/v2/users/" + userId + "/roles";

        Map<String, List<String>> body = Map.of("roles", List.of(roleId));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    private String getRoleId(String roleName, String token) {
        String url = domain + "/api/v2/roles?name_filter=" + roleName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        List<Map<String, Object>> roles = response.getBody();

        if (roles != null && !roles.isEmpty()) {
            return roles.getFirst().get("id").toString();
        }
        return null;
    }

    @Data
    public static class Auth0TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
    }

    @Data
    public static class User {
        private String email;
        private String password;
        private String role;
    }
}
