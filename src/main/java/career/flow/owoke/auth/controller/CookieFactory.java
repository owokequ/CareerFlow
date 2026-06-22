package career.flow.owoke.auth.controller;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Configuration
public class CookieFactory {

    @Bean
    public ResponseCookie setCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false) // true на проде
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // true на проде
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
