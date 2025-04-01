package org.phong.horizon.infrastructure.config;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.infrastructure.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    private final AuthService authService;

    public JpaAuditConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        // Return an implementation of AuditorAware that uses your AuthService
        // to get the current user's UUID.
        // This lambda avoids the need for a separate class.
        return () -> {
            try {
                // Attempt to get the user ID from AuthService
                // IMPORTANT: Handle potential null if no user is authenticated (e.g., during startup)
                UUID userId = authService.getUserId();
                return Optional.ofNullable(userId);
            } catch (Exception e) {
                // Log the exception if needed, but return empty for auditing
                // This might happen if called outside a request context or before authentication
                 log.warn("Could not get auditor ID: {}", e.getMessage());
                return Optional.empty();
            }
        };
    }
}