package career.flow.owoke.auth.service;

import java.time.Duration;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import career.flow.owoke.config.security.PasswordHash;
import career.flow.owoke.user.dto.request.UserCreateRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PasswordHash passwordHash;
    private final JwtService jwtService;
    private final RedisService redisService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AuthUser user = authRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        return new User(user.getEmail(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

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
        AuthUser user = authRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException(dto.email()));

        JwtClaims claims = new JwtClaims(
                user.getId(),
                user.getName(),
                user.getEmail(),
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
}
