package career.flow.owoke.config.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.refresh-token")
public record RefreshTokenProperties(
        String keyPrefix,
        Duration ttl) {

}
