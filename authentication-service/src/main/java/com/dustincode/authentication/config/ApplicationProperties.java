package com.dustincode.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "application", ignoreInvalidFields = true)
public class ApplicationProperties {

    private final JWT jwt = new JWT();

    @Getter @Setter
    public static class JWT {
        private String base64Secret;
        private long tokenValidityInSeconds;
    }
}
