package com.dev.monkey_dev.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.dto.response.UserResponseDto;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmailOrUsername(String email, String username);

    @Query("SELECT new com.dev.monkey_dev.dto.response.UserResponseDto(u.id, u.fullName, u.username, u.email, u.active) FROM Users u WHERE u.active = :isActive")
    List<UserResponseDto> findAllUserIsActive(@Param("isActive") Boolean isActive);

}
