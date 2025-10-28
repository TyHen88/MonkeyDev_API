package com.dev.monkey_dev.payload.auth;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public abstract String getDeviceType();

    public abstract String getBrowserName();

    public abstract String getCountry();

    public abstract String getCity();

    public abstract String getIpAddress();
}