package career.flow.owoke.auth.service;

import org.springframework.stereotype.Service;

import career.flow.owoke.common.util.HashUtils;
import career.flow.owoke.config.security.RefreshTokenProperties;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final RedisService redisService;
    private final RefreshTokenProperties properties;

    public void saveToken(String authId, String refreshToken) {
        redisService.save(key(authId), hash(refreshToken), properties.ttl());
    }

    public void saveHash(String authId, String refreshTokenHash) {
        redisService.save(key(authId), refreshTokenHash, properties.ttl());
    }

    public boolean matches(String authId, String refreshToken) {
        String storedHash = redisService.get(key(authId));
        return storedHash != null && storedHash.equals(hash(refreshToken));
    }

    public boolean exists(String authId) {
        return redisService.exists(key(authId));
    }

    public void delete(String authId) {
        redisService.delete(key(authId));
    }

    public String hash(String token) {
        return HashUtils.sha256(token);
    }

    private String key(String authId) {
        return properties.keyPrefix() + authId;
    }
}
