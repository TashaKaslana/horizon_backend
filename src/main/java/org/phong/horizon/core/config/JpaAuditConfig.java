package org.phong.horizon.core.config;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.InternalUserIdEnhancerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                log.trace("No authenticated user found for auditing.");
                return Optional.empty();
            }

            // Attempt to retrieve the ID from the details map added by the filter
            Object details = authentication.getDetails();
            if (details instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> detailsMap = (Map<String, Object>) details;
                Object userIdObj = detailsMap.get(InternalUserIdEnhancerFilter.USER_ID_DETAIL_KEY); // Use the key constant

                if (userIdObj instanceof UUID userId) { // Use pattern variable `userId`
                    log.trace("Auditing using user ID from context: {}", userId);
                    return Optional.of(userId);
                } else {
                    log.warn("Internal user ID key '{}' not found or not a UUID in authentication details map. Details: {}",
                            InternalUserIdEnhancerFilter.USER_ID_DETAIL_KEY, detailsMap);
                    return Optional.empty(); // Cannot determine auditor
                }
            } else {
                log.warn("Authentication details are not of expected type Map. Cannot extract internal user ID for auditing. Details: {}", details);
                return Optional.empty(); // Cannot determine auditor
            }
        };
    }
}