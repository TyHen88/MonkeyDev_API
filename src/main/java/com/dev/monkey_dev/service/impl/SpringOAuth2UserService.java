package com.dev.monkey_dev.service.impl;

import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.enums.AuthProvider;
import com.dev.monkey_dev.payload.auth.GoogleOAuth2UserInfo;
import com.dev.monkey_dev.payload.auth.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Spring Security OAuth2UserService implementation that integrates with our
 * custom user service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpringOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, attributes);

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<Users> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        Users user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user if provider doesn't match
            if (!user.getAuthProvider().equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
                throw new OAuth2AuthenticationException(
                        "Looks like you're signed up with " + user.getAuthProvider() + " account. Please use your " +
                                user.getAuthProvider() + " account to login.");
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
            throw new OAuth2AuthenticationException(
                    "Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

    /**
     * Register a new user using oAuth2UserInfo and registrationId.
     */
    private Users registerNewUser(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
        Users user = new Users();
        user.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        user.setEmail(oAuth2UserInfo.getEmail());

        // Split name into first and last name
        String name = oAuth2UserInfo.getName();
        if (oAuth2UserInfo instanceof GoogleOAuth2UserInfo googleUserInfo) {
            user.setFullName(googleUserInfo.getGivenName() + " " + googleUserInfo.getFamilyName());
            user.setUsername(googleUserInfo.getEmail().split("@")[0].toLowerCase());
        } else if (name != null) {
            String[] nameParts = name.split(" ", 2);
            user.setFullName(nameParts[0] + " " + (nameParts.length > 1 ? nameParts[1] : ""));
            user.setUsername(nameParts[0].toLowerCase());
        }

        user.activate();

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
