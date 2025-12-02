package com.dev.monkey_dev.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public CustomJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Keep "SCOPE_" prefix for scope-based authorities, we'll handle role
        // separately
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");

        this.jwtAuthenticationConverter = new JwtAuthenticationConverter();
        this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        // Extract authorities via internal converter (for scope claims, if present)
        AbstractAuthenticationToken token = jwtAuthenticationConverter.convert(jwt);
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        // Extract role from JWT and add as ROLE_ authority
        String role = jwt.getClaimAsString("role");
        if (role != null && !role.isBlank()) {
            // Add ROLE_ prefix for Spring Security role-based authorization
            SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

            // Combine scope authorities (if any) with role authority
            // Note: Scope authorities will have "SCOPE_" prefix from JwtGrantedAuthoritiesConverter
            // Role authorities have "ROLE_" prefix and are separate from scopes
            authorities = Stream.concat(
                    authorities.stream(),
                    Stream.of(roleAuthority)).collect(Collectors.toList());
        }

        // Extract username (or fallback to sub)
        String principalName = jwt.getClaimAsString("username");
        if (principalName == null || principalName.isBlank()) {
            principalName = jwt.getSubject(); // fallback to "sub"
        }

        // Create a new JwtAuthenticationToken with custom principal name and
        // authorities
        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }
}
