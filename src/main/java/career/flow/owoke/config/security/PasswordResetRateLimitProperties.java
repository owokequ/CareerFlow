package career.flow.owoke.config.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.password-reset.rate-limit")
public record PasswordResetRateLimitProperties(
        boolean enabled,
        String keyPrefix,
        Duration cooldown) {

}
