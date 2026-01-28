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
import java.util.List;
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

        // Extract roles from JWT and add ROLE_ authorities
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null || roles.isEmpty()) {
            String role = jwt.getClaimAsString("role");
            roles = role != null && !role.isBlank() ? List.of(role) : List.of();
        }

        if (!roles.isEmpty()) {
            List<GrantedAuthority> roleAuthorities = roles.stream()
                    .filter(r -> r != null && !r.isBlank())
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                    .collect(Collectors.toList());
            authorities = Stream.concat(authorities.stream(), roleAuthorities.stream())
                    .collect(Collectors.toList());
        }

        // Extract permissions and add as authorities (no prefix)
        List<String> permissions = jwt.getClaimAsStringList("permissions");
        if (permissions != null && !permissions.isEmpty()) {
            List<GrantedAuthority> permissionAuthorities = permissions.stream()
                    .filter(p -> p != null && !p.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            authorities = Stream.concat(authorities.stream(), permissionAuthorities.stream())
                    .collect(Collectors.toList());
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
