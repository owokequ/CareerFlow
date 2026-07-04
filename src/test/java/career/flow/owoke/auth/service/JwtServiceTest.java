package career.flow.owoke.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import career.flow.owoke.auth.dto.JwtClaims;
import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKeyAccess",
                "test-access-secret-must-be-at-least-32-bytes-long");
        ReflectionTestUtils.setField(jwtService, "secretKeyRefresh",
                "test-refresh-secret-must-be-at-least-32-bytes-long");
        ReflectionTestUtils.setField(jwtService, "timeLifeAccess", 60_000L);
        ReflectionTestUtils.setField(jwtService, "timeLifeRefresh", 120_000L);
    }

    @Test
    void generatedAccessTokenCanBeValidatedWithConfiguredSecret() {
        JwtClaims jwtClaims = new JwtClaims(
                "user-id",
                "Artem",
                "artem@example.com",
                true,
                List.of("ROLE_USER"));

        String token = jwtService.generateAccessToken(jwtClaims);

        Claims claims = jwtService.getAllClaims(token, "access");
        assertThat(claims.getSubject()).isEqualTo("user-id");
        assertThat(claims.get("emailVerified", Boolean.class)).isTrue();
        assertThat(jwtService.getRoles(token, "access")).containsExactly("ROLE_USER");
    }
}
