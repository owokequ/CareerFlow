package career.flow.owoke.config.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.refresh-cookie")
public record RefreshCookieProperties(
        boolean secure,
        boolean httpOnly,
        String sameSite,
        String path,
        Duration maxAge) {
}