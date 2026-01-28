package com.dev.monkey_dev.domain.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Builder;
import com.dev.monkey_dev.domain.entity.Permission;
import com.dev.monkey_dev.domain.entity.Role;

@Builder
public record SecurityUser(Users users) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (users.getRoles() != null) {
            authorities.addAll(users.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toSet()));
            authorities.addAll(users.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .collect(Collectors.toSet()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.users.getPassword();
    }

    @Override
    public String getUsername() {
        return this.users.getUsername();
    }

    public Long getUserId() {
        return this.users.getId();
    }

    public String getEmail() {
        return this.users.getEmail();
    }

    public String getRole() {
        if (users.getRoles() == null || users.getRoles().isEmpty()) {
            return null;
        }
        return users.getRoles().iterator().next().getName();
    }

    public Set<String> getRoles() {
        if (users.getRoles() == null) {
            return Set.of();
        }
        return users.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
    }

    public Set<String> getPermissions() {
        if (users.getRoles() == null) {
            return Set.of();
        }
        return users.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return users.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return users.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return users.isActive();
    }

    @Override
    public boolean isEnabled() {
        return users.isActive();
    }
}
