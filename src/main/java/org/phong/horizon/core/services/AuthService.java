package org.phong.horizon.core.services;

import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.enums.Role;
import org.phong.horizon.user.enums.UserErrorEnums;
import org.phong.horizon.user.exceptions.UserNotFoundException;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> getUserClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new InsufficientAuthenticationException("JWT Token not found in security context");
        }

        return jwt.getClaims();
    }

    public String getAuth0Id() {
        return (String) getUserClaims().get("sub");
    }

    @Transactional
    public User getUser() {
        return userRepository.findByAuth0Id(getAuth0Id())
                .orElseThrow(() -> {
                    log.error("UserNotFoundException for Auth0 ID: {}", getAuth0Id());
                    return new UserNotFoundException(UserErrorEnums.USER_NOT_FOUND.getMessage());
                });
    }

    public UUID getUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            Object userIdObj = details.get(InternalUserIdEnhancerFilter.USER_ID_DETAIL_KEY);
            if (userIdObj instanceof UUID) {
                log.trace("Retrieved internal user ID {} from security context details", userIdObj);
                return (UUID) userIdObj;
            }
        }
        log.warn("Internal user ID not found in security context details.");
        return null;
    }

    public boolean isPrincipal(UUID userId) {
        return getUserIdFromContext().equals(userId);
    }

    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role.getRole()));
    }
}