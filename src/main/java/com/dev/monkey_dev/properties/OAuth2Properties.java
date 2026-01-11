package com.dev.monkey_dev.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {
    private String authorizedRedirectUrl;

    private Google google = new Google();

    @Getter
    @Setter
    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;

        // Defaults (you can also put them in yml)
        private String authUrl = "https://accounts.google.com/o/oauth2/v2/auth";
        private String tokenUrl = "https://oauth2.googleapis.com/token";
        private String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
    }
}
