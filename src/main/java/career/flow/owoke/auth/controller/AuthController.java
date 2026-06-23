package career.flow.owoke.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import career.flow.owoke.auth.dto.request.AuthUserCreateRequest;
import career.flow.owoke.auth.dto.request.AuthUserLoginRequest;
import career.flow.owoke.auth.dto.request.ForgotPasswordRequest;
import career.flow.owoke.auth.dto.request.ResetPasswordRequest;
import career.flow.owoke.auth.dto.response.AuthUserResponse;
import career.flow.owoke.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;
    private final CookieFactory cookieFactory;

    @PostMapping("register")
    public ResponseEntity<AuthUserResponse> createUser(@RequestBody AuthUserCreateRequest dto) {
        AuthUserResponse result = authService.createUser(dto);
        cookieFactory.createRefreshCookie(result.refreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("login")
    public ResponseEntity<AuthUserResponse> loginUser(@RequestBody AuthUserLoginRequest dto) {
        AuthUserResponse result = authService.loginUser(dto);
        cookieFactory.createRefreshCookie(result.refreshToken());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("logout/{id}")
    public ResponseEntity<String> logoutUser(@PathVariable String id) {
        cookieFactory.clearRefreshCookie();
        return ResponseEntity.status(204).body(authService.logoutUser(id));
    }

    @GetMapping("register/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.verifyUser(token));
    }

    @PostMapping("password/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
            @RequestBody ResetPasswordRequest pass) {
        return ResponseEntity.ok(authService.resetPassword(token, pass));
    }

    @PostMapping("password/forgot")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest email) {
        authService.forgotPassword(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthUserResponse> refreshToken(@CookieValue("refresh_token") String token) {
        AuthUserResponse result = authService.refreshToken(token);
        cookieFactory.createRefreshCookie(result.refreshToken());
        return ResponseEntity.ok(result);
    }

}
