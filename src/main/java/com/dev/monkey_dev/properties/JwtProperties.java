package com.dev.monkey_dev.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuration properties for JWT settings.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @DurationUnit(ChronoUnit.SECONDS) Duration expiration,
        @DurationUnit(ChronoUnit.SECONDS) Duration refreshExpiration
) {

}