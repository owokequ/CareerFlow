package career.flow.owoke.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import career.flow.owoke.auth.dto.request.AuthUserCreateRequest;
import career.flow.owoke.auth.entity.AuthUser;
import career.flow.owoke.auth.repository.AuthRepository;
import career.flow.owoke.config.security.PasswordHash;
import career.flow.owoke.messaging.EmailService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private PasswordHash passwordHash;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void createUserSavesUserBeforeStoringVerificationToken() {
        AuthUserCreateRequest request = new AuthUserCreateRequest(
                "Artem",
                "artem@example.com",
                "password123");

        when(passwordHash.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(authRepository.save(any(AuthUser.class))).thenAnswer(invocation -> {
            AuthUser user = invocation.getArgument(0);
            user.setId("auth-id");
            return user;
        });
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        authService.createUser(request);

        var inOrder = inOrder(authRepository, redisService, emailService);
        inOrder.verify(authRepository).save(any(AuthUser.class));
        inOrder.verify(redisService).save(
                org.mockito.ArgumentMatchers.startsWith("verify:"),
                eq("auth-id"),
                any());
        inOrder.verify(emailService).sendVerificationEmail(eq("artem@example.com"), any());
    }
}
