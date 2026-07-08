package career.flow.owoke.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import career.flow.owoke.auth.dto.request.AuthUserCreateRequest;
import career.flow.owoke.auth.entity.AuthUser;
import career.flow.owoke.auth.event.AuthSessionCreatedEvent;
import career.flow.owoke.auth.event.AuthUserRegisteredEvent;
import career.flow.owoke.auth.repository.AuthRepository;
import career.flow.owoke.config.security.PasswordResetRateLimitProperties;
import career.flow.owoke.config.security.PasswordHash;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordHash passwordHash;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private PasswordResetRateLimitProperties passwordResetRateLimitProperties;

    @Mock
    private RefreshTokenStore refreshTokenStore;

    @InjectMocks
    private AuthService authService;

    @Test
    void createUserSavesUserBeforePublishingRegistrationEvents() {
        AuthUserCreateRequest request = new AuthUserCreateRequest(
                "Artem",
                "Artem@Example.com",
                "password123");

        when(passwordHash.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(authRepository.save(any(AuthUser.class))).thenAnswer(invocation -> {
            AuthUser user = invocation.getArgument(0);
            user.setId("auth-id");
            return user;
        });
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(refreshTokenStore.hash("refresh-token")).thenReturn("refresh-token-hash");

        authService.createUser(request);

        var inOrder = inOrder(authRepository, eventPublisher);
        inOrder.verify(authRepository).save(any(AuthUser.class));
        inOrder.verify(eventPublisher).publishEvent(new AuthUserRegisteredEvent(
                "auth-id",
                "Artem",
                "artem@example.com"));
        inOrder.verify(eventPublisher).publishEvent(new AuthSessionCreatedEvent(
                "auth-id",
                "refresh-token-hash"));
    }
}
