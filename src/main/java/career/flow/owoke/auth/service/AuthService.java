package career.flow.owoke.auth.service;

import java.time.Duration;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.auth.dto.JwtClaims;
import career.flow.owoke.auth.dto.request.AuthUserCreateRequest;
import career.flow.owoke.auth.dto.request.AuthUserLoginRequest;
import career.flow.owoke.auth.dto.response.AuthUserResponse;
import career.flow.owoke.auth.entity.AuthUser;
import career.flow.owoke.auth.enums.AuthRole;
import career.flow.owoke.auth.repository.AuthRepository;
import career.flow.owoke.common.exception.userExceptions.UserAlreadyExistsException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.config.security.PasswordHash;
import career.flow.owoke.messaging.EmailService;
import career.flow.owoke.user.dto.request.UserCreateRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PasswordHash passwordHash;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final EmailService emailService;

    @Transactional
    public AuthUserResponse createUser(AuthUserCreateRequest request) {
        if (authRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        AuthUser user = new AuthUser();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordHash.passwordEncoder().encode(request.password()));
        user.setRole(AuthRole.USER);
        authRepository.save(user);

        kafkaTemplate.send("auth", new UserCreateRequest(
                user.getId(),
                user.getName(),
                user.getEmail()));

        JwtClaims claims = new JwtClaims(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEmailVerified(),
                List.of("ROLE_" + user.getRole().name()));

        String accessToken = jwtService.generateAccessToken(claims);
        String refreshToken = jwtService.generateRefreshToken(claims);

        redisService.save("refresh:" + user.getId(), refreshToken, Duration.ofDays(30));

        emailService.sendVerificationEmail(user.getEmail(), refreshToken);

        return new AuthUserResponse(
                accessToken,
                refreshToken);
    }

    @Transactional
    public AuthUserResponse loginUser(AuthUserLoginRequest dto) {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AuthUser user = (AuthUser) auth.getPrincipal();

        JwtClaims claims = new JwtClaims(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEmailVerified(),
                List.of("ROLE_" + user.getRole().name()));

        String accessToken = jwtService.generateAccessToken(claims);
        String refreshToken = jwtService.generateRefreshToken(claims);

        redisService.save("refresh:" + user.getId(), refreshToken, Duration.ofDays(30));

        return new AuthUserResponse(
                accessToken,
                refreshToken);
    }

    @Transactional(readOnly = true)
    public String logoutUser(String userId) {
        if (redisService.exists("refresh:" + userId)) {
            redisService.delete("refresh:" + userId);
            return "Logged out successfully";
        } else {
            throw new RuntimeException("User not found");
        }

    }

    public AuthUserResponse refreshToken(String token) {
        Claims claims = jwtService.getAllClaims(token, "refresh");
        JwtClaims jwtClaims = new JwtClaims(
                claims.getSubject(),
                claims.get("name", String.class),
                claims.get("email", String.class),
                claims.get("emailVerified", Boolean.class),
                claims.get("roles", List.class));

        String refreshVerify = redisService.get("refresh:" + jwtClaims.id());

        if (!refreshVerify.equals(token)) {
            throw new RuntimeException("Refresh not found with userId: " + jwtClaims.id());
        }

        String access = jwtService.generateAccessToken(jwtClaims);
        String refresh = jwtService.generateRefreshToken(jwtClaims);

        redisService.save("refresh:" + jwtClaims.id(), refresh, Duration.ofDays(30));

        return new AuthUserResponse(
                access,
                refresh);
    }

    public String verifyUser(String token) {
        String email = jwtService.getAllClaims(token, "refresh").get("email", String.class);
        AuthUser user = authRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        user.setEmailVerified(true);
        authRepository.save(user);
        return "User verified successfully";
    }
}
