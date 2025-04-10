package org.phong.horizon.core.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint!";
    }

    @GetMapping("/private")
    public String privateEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return "Hello, " + jwt.getClaim("email");
    }

    @GetMapping("/user/role")
    public String getCurrentUserRole() {
        Optional<String> userRole = getUserRole();

        // Return the role if found, otherwise a message indicating no role is assigned
        return userRole.orElse("No role assigned to the user");
    }

    public Optional<String> getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();  // Return empty if no JWT is found
        }

        // Extract roles from the JWT claims
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();

        // Assuming roles are stored as granted authorities, get the first role
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))  // Filter out roles (e.g., "ROLE_ADMIN")
                .findFirst();
    }
}
