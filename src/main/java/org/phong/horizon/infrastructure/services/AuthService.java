package org.phong.horizon.infrastructure.services;

import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> getUserClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("JWT Token not found in security context");
        }

        return jwt.getClaims();
    }

    public String getAuth0Id() {
        return (String) getUserClaims().get("sub");
    }

    public User getUser() {
        return userRepository.findByAuth0Id(
                getAuth0Id()).orElseThrow(() -> new UserNotFoundException(UserErrorEnums.USER_NOT_FOUND.getMessage())
        );
    }

    public UUID getUserId() {
        try {
            return getUser().getId();
        } catch (UserNotFoundException e) {
            return null;
        }
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role));
    }
}