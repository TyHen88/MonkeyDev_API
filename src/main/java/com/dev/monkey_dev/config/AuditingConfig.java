package com.dev.monkey_dev.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new SecurityAuditorAware();
    }

    static class SecurityAuditorAware implements AuditorAware<Long> {
        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                String userId = jwt.getClaimAsString("id");
                if (userId != null) {
                    try {
                        return Optional.of(Long.parseLong(userId));
                    } catch (NumberFormatException ignored) {
                        return Optional.empty();
                    }
                }
            }
            return Optional.empty();
        }
    }
}
