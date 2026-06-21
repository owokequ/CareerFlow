package career.flow.owoke.auth.controller;

import org.springframework.http.ResponseEntity;
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

    @PostMapping("register")
    public ResponseEntity<AuthUserResponse> createUser(@RequestBody AuthUserCreateRequest dto) {
        return ResponseEntity.ok(authService.createUser(dto));
    }

    @PostMapping("login")
    public ResponseEntity<AuthUserResponse> loginUser(@RequestBody AuthUserLoginRequest dto) {
        return ResponseEntity.ok(authService.loginUser(dto));
    }

    @DeleteMapping("logout/{id}")
    public ResponseEntity<String> logoutUser(@PathVariable String id) {
        return ResponseEntity.status(204).body(authService.logoutUser(id));
    }

    @GetMapping("register/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.verifyUser(token));
    }

}
