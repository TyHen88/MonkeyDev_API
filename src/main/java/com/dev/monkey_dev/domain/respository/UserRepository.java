package com.dev.monkey_dev.domain.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import com.dev.monkey_dev.domain.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.id = :id and u.active = true")
    Optional<Users> findUserById(Long id);

    @EntityGraph(attributePaths = { "roles", "roles.permissions" })
    Optional<Users> findByEmail(String email);

    @EntityGraph(attributePaths = { "roles", "roles.permissions" })
    List<Users> findByUsername(String username);

    @EntityGraph(attributePaths = { "roles", "roles.permissions" })
    Optional<Users> findWithRolesById(Long id);

    Optional<Users> findByEmailOrUsername(String email, String username);

    @Query("SELECT DISTINCT u FROM Users u WHERE " +
            "(:isActive IS NULL OR u.active = :isActive) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND NOT EXISTS (SELECT r FROM u.roles r WHERE r.name = 'ADMIN')")
    Page<Users> findAllUsersWithFilters(
            @Param("isActive") Boolean isActive,
            @Param("search") String search,
            Pageable pageable);
}
