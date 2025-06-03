package org.phong.horizon.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {
    private String domain;
    private String clientId;
}
