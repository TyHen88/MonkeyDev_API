package com.dev.monkey_dev.domain.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import lombok.Builder;

@Builder
public record SecurityUser(Users users) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()));
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
        return this.users.getRole().name();
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
