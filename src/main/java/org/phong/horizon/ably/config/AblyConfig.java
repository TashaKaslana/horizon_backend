package org.phong.horizon.ably.config;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AblyConfig {

    @Value("${ably.api.key}")
    private String ablyApiKey;

    @Bean
    public AblyRealtime ablyRealtime() throws AblyException {
        ClientOptions options = new ClientOptions(ablyApiKey);
        return new AblyRealtime(options);
    }
}

