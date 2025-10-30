package com.dev.monkey_dev.domain.entity;

import com.dev.monkey_dev.enums.AuthProvider;

import com.dev.monkey_dev.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Users entity.
 *
 * Notes:
 * - Keep the class name as "Users" for backward compatibility with the rest of
 *
 * the project.
 *
 * - Consider renaming to "User" (and adjusting references) for conventional
 * naming in a future refactor.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "password")
@EqualsAndHashCode(callSuper = true)
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "email", length = 150, nullable = false)
    private String email;
    //

    // Exclude password from JSON serialization and toString to avoid accidental
    // leakage.
    // Password is optional for OAuth2 users, so we don't use @NotBlank here
    @Size(min = 8, max = 255)
    @JsonIgnore
    @Column(name = "password", length = 255, nullable = true)
    //
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Roles role = Roles.USER;

    // Name the field 'active' (maps to is_active column) so Lombok generates a
    // natural isActive() getter.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // Convenience methods
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Roles getRole() {
        return role;
    }
}