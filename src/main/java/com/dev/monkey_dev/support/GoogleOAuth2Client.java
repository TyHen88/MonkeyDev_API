package com.dev.monkey_dev.support;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.dev.monkey_dev.properties.OAuth2Properties;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class GoogleOAuth2Client {

    private final OAuth2Properties props;
    private final RestTemplate restTemplate;

    public String exchangeCodeForAccessToken(String code) {
        OAuth2Properties.Google google = props.getGoogle();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", google.getClientId());
        form.add("client_secret", google.getClientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", google.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> res = restTemplate.postForObject(google.getTokenUrl(), request, Map.class);

        if (res == null || res.get("access_token") == null) {
            throw new RuntimeException("Failed to exchange code for token");
        }
        return String.valueOf(res.get("access_token"));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserInfo(String accessToken) {
        String url = props.getGoogle().getUserInfoUrl() + "?access_token=" + accessToken;
        Map<String, Object> userInfo = restTemplate.getForObject(url, Map.class);

        if (userInfo == null) {
            throw new RuntimeException("Failed to fetch Google user info");
        }
        return userInfo;
    }
}
