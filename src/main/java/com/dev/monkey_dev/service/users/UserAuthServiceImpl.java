package com.dev.monkey_dev.service.users;

import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SecurityUser loadUserByUsername(String username) {
        Users user = resolveUser(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new SecurityUser(user);

    }

    private Users resolveUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        if (username.contains("@")) {
            return userRepository.findByEmail(username).orElse(null);
        }
        List<Users> users = userRepository.findByUsername(username);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return userRepository.findByEmail(username).orElse(null);
    }
}
