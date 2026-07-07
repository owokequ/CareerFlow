package career.flow.owoke.auth.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import career.flow.owoke.config.security.RefreshCookieProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieFactory {

    private final RefreshCookieProperties properties;

    public ResponseCookie createRefreshCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(properties.httpOnly())
                .secure(properties.secure())
                .path(properties.path())
                .maxAge(properties.maxAge())
                .sameSite(properties.sameSite())
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(properties.httpOnly())
                .secure(properties.secure())
                .path(properties.path())
                .maxAge(0)
                .sameSite(properties.sameSite())
                .build();
    }
}
