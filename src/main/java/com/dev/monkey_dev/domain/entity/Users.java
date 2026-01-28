package com.dev.monkey_dev.domain.entity;

import com.dev.monkey_dev.enums.AuthProvider;

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
import java.util.HashSet;
import java.util.Set;

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
@ToString(exclude = { "password", "roles" })
@EqualsAndHashCode(callSuper = true, exclude = "roles")
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

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "auth_provider", length = 50)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

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

}
