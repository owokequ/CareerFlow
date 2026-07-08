package career.flow.owoke.auth.service;

import java.time.Duration;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.auth.dto.JwtClaims;
import career.flow.owoke.auth.dto.request.AuthUserCreateRequest;
import career.flow.owoke.auth.dto.request.AuthUserLoginRequest;
import career.flow.owoke.auth.dto.request.ForgotPasswordRequest;
import career.flow.owoke.auth.dto.request.ResetPasswordRequest;
import career.flow.owoke.auth.dto.response.AuthUserResponse;
import career.flow.owoke.auth.entity.AuthUser;
import career.flow.owoke.auth.enums.AuthRole;
import career.flow.owoke.auth.event.AuthUserRegisteredEvent;
import career.flow.owoke.auth.event.PasswordResetRequestedEvent;
import career.flow.owoke.auth.repository.AuthRepository;
import career.flow.owoke.common.exception.authExceptions.InvalidRefreshTokenException;
import career.flow.owoke.common.exception.authExceptions.RefreshTokenNotFoundException;
import career.flow.owoke.common.exception.userExceptions.InvalidVerificationTokenException;
import career.flow.owoke.common.exception.userExceptions.UserAlreadyExistsException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.common.util.CustomUserDetails;
import career.flow.owoke.config.security.PasswordHash;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordHash passwordHash;
    private final JwtService jwtService;
    private final RedisService redisService;

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

        eventPublisher.publishEvent(new AuthUserRegisteredEvent(
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

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        AuthUser user = principal.getUser();

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

    @Transactional
    public String logoutUser(String userId) {
        if (redisService.exists("refresh:" + userId)) {
            redisService.delete("refresh:" + userId);
            return "Logged out successfully";
        } else {
            throw new RefreshTokenNotFoundException(userId);
        }

    }

    public AuthUserResponse refreshToken(String token) {
        Claims claims;
        try {
            claims = jwtService.getAllClaims(token, "refresh");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }

        JwtClaims jwtClaims = new JwtClaims(
                claims.getSubject(),
                claims.get("name", String.class),
                claims.get("email", String.class),
                claims.get("emailVerified", Boolean.class),
                claims.get("roles", List.class));

        String refreshVerify = redisService.get("refresh:" + jwtClaims.id());

        if (refreshVerify == null || !refreshVerify.equals(token)) {
            throw new RefreshTokenNotFoundException(jwtClaims.id());
        }

        String access = jwtService.generateAccessToken(jwtClaims);
        String refresh = jwtService.generateRefreshToken(jwtClaims);

        redisService.save("refresh:" + jwtClaims.id(), refresh, Duration.ofDays(30));

        return new AuthUserResponse(
                access,
                refresh);
    }

    public String resetPassword(String token, ResetPasswordRequest dto) {
        String userId = redisService.get("reset:" + token);
        if (userId == null) {
            throw new InvalidVerificationTokenException(token);
        }
        redisService.delete("reset:" + token);
        AuthUser user = authRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setPassword(passwordHash.passwordEncoder().encode(dto.newPassword()));
        authRepository.save(user);
        return "Password reset successfully";
    }

    @Transactional(readOnly = true)
    public void forgotPassword(ForgotPasswordRequest dto) {
        AuthUser user = authRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException(dto.email()));
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(user.getId(), user.getEmail()));
    }

    public String verifyUser(String token) {
        String userId = redisService.get("verify:" + token);
        if (userId == null) {
            throw new InvalidVerificationTokenException(token);
        }
        AuthUser user = authRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setEmailVerified(true);
        authRepository.save(user);

        redisService.delete("verify:" + token);
        return "User verified successfully";
    }
}
