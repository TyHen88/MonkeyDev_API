package com.ezcart.api.service.auth;

import com.ezcart.api.common.api.StatusCode;
import com.ezcart.api.domain.respository.RoleRepository;
import com.ezcart.api.domain.respository.UserRepository;
import com.ezcart.api.domain.entity.Users;
import com.ezcart.api.enums.AuthProvider;
import com.ezcart.api.exception.BusinessException;
import com.ezcart.api.payload.auth.GoogleOAuth2UserInfo;
import com.ezcart.api.payload.auth.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Fixed CustomOAuth2UserService to remove dependency on missing OAuth2 classes.
 * Instead, add a loadUser method that accepts a registrationId and attribute
 * map.
 * This enables using this logic in contexts where
 * org.springframework.security.oauth2.client cannot be imported.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Simulates the loading of a user, taking registrationId and oauth2 user
     * attributes.
     */
    public OAuth2UserPrincipal loadUser(String registrationId, Map<String, Object> attributes) {
        try {
            return processOAuth2User(registrationId, attributes);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new BusinessException(StatusCode.VALIDATION_FAILED);
        }
    }

    private OAuth2UserPrincipal processOAuth2User(String registrationId, Map<String, Object> attributes) {
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, attributes);

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new BusinessException(StatusCode.OAUTH_EMAIL_NOT_FOUND);
        }

        Optional<Users> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        Users user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user if provider doesn't match
            if (!user.getAuthProvider().equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
                throw new BusinessException(StatusCode.OAUTH_PROVIDER_MISMATCH);
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(registrationId, oAuth2UserInfo);
        }

        return new OAuth2UserPrincipal(user, attributes);
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new BusinessException(StatusCode.OAUTH_PROVIDER_NOT_SUPPORTED);
        }
    }

    /**
     * Register a new user using oAuth2UserInfo and registrationId.
     */
    private Users registerNewUser(String registrationId, OAuth2UserInfo oAuth2UserInfo) {

        Users user = new Users();
        user.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setFullName(oAuth2UserInfo.getName());

        user.setUsername(oAuth2UserInfo.getEmail().split("@")[0].toLowerCase());
        user.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        var roles = roleRepository.findAllByNameIn(java.util.Set.of("USER"));
        if (roles.isEmpty()) {
            throw new BusinessException(StatusCode.DEFAULT_ROLE_NOT_CONFIGURED);
        }
        user.setRoles(new java.util.HashSet<>(roles));
        user.activate();

        // For OAuth2 users, we don't set a password as they authenticate through the
        // provider
        // The password field will be null, which is now allowed by the entity
        // validation

        return userRepository.save(user);
    }

    /**
     * Update existing user information, such as profile image.
     */
    private Users updateExistingUser(Users existingUser, OAuth2UserInfo oAuth2UserInfo) {
        if (oAuth2UserInfo.getImageUrl() != null) {
            existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        }
        return userRepository.save(existingUser);
    }
}

