package org.phong.horizon.core.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.user.infrastructure.persistence.repositories.UserRepository; // Assuming this repo has getUserIdByAuth0Id
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component; // Make it a Spring bean
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component // Register this filter as a Spring bean
@RequiredArgsConstructor // Lombok constructor injection
public class InternalUserIdEnhancerFilter extends OncePerRequestFilter {

    public static final String USER_ID_DETAIL_KEY = "internalUserId"; // Key for storing the ID

    private final UserRepository userRepository; // Inject repository to find the user

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        // Only enhance if authentication exists, is authenticated, and is a JwtAuthenticationToken
        // AND if we haven't already added the detail (in case filter runs multiple times somehow)
        if (authentication != null &&
                authentication.isAuthenticated() &&
                authentication instanceof JwtAuthenticationToken jwtAuth &&
                !(authentication.getDetails() instanceof Map && ((Map<?, ?>) authentication.getDetails()).containsKey(USER_ID_DETAIL_KEY)))
        {
            try {
                Jwt jwt = jwtAuth.getToken();
                String auth0Id = jwt.getSubject(); // "sub" claim is the Auth0 ID

                if (auth0Id != null) {
                    // Perform the lookup ONCE per request here
                    UUID internalUserId = userRepository.getUserIdByAuth0Id(auth0Id); // Use your repo method

                    if (internalUserId != null) {
                        log.trace("Found internal user ID {} for auth0 ID {}", internalUserId, auth0Id);
                        JwtAuthenticationToken enhancedAuthentication = getJwtAuthenticationToken(authentication, internalUserId, jwt);

                        // Update the Security Context with the enhanced Authentication object
                        context.setAuthentication(enhancedAuthentication);
                        log.trace("Updated SecurityContext with internal user ID for: {}", auth0Id);

                    } else {
                        // User authenticated via Auth0, but not found in local DB. This might be okay
                        // depending on your user provisioning flow (e.g., user exists in Auth0 but hasn't
                        // completed local profile setup yet). Don't add the detail.
                        log.warn("Authenticated user with auth0 ID {} not found in local user repository. Cannot enhance token with internal ID.", auth0Id);
                    }
                } else {
                     log.warn("Auth0 ID (sub claim) not found in JWT.");
                }
            } catch (Exception e) {
                 // Log error but don't break the filter chain
                 log.error("Error enhancing authentication token with internal user ID", e);
            }
        } else if (authentication != null && authentication.getDetails() instanceof Map && ((Map<?, ?>) authentication.getDetails()).containsKey(USER_ID_DETAIL_KEY)) {
             log.trace("Internal User ID already present in Authentication details.");
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private static JwtAuthenticationToken getJwtAuthenticationToken(Authentication authentication, UUID internalUserId, Jwt jwt) {
        Map<String, Object> details = new HashMap<>();
        if (authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            var authenticationDetails = (Map<String, Object>) authentication.getDetails();
            details.putAll(authenticationDetails);
        }
        details.put(USER_ID_DETAIL_KEY, internalUserId);

        // Create a NEW Authentication token with the added details
        JwtAuthenticationToken enhancedAuthentication = new JwtAuthenticationToken(
                jwt,
                authentication.getAuthorities(),
                jwt.getSubject() // Principal name remains the subject
        );
        enhancedAuthentication.setDetails(details); // Set the enhanced details
        return enhancedAuthentication;
    }
}