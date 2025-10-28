package com.dev.monkey_dev.payload.auth;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    public String getGivenName() {
        return (String) attributes.get("given_name");
    }

    public String getFamilyName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getDeviceType() {
        return (String) attributes.get("device_type");
    }

    @Override
    public String getBrowserName() {
        return (String) attributes.get("browser_name");
    }

    @Override
    public String getCountry() {
        return (String) attributes.get("country");
    }

    @Override
    public String getCity() {
        return (String) attributes.get("city");
    }

    @Override
    public String getIpAddress() {
        return (String) attributes.get("ip_address");
    }
}