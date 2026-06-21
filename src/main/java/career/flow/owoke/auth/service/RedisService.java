package career.flow.owoke.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redis;

    public void save(
            String key,
            String value,
            Duration ttl) {
        redis.opsForValue().set(
                key,
                value,
                ttl);
    }

    public String get(String key) {
        return redis.opsForValue().get(key);
    }

    public void delete(String key) {
        redis.delete(key);
    }

    public boolean exists(String key) {
        return redis.hasKey(key);
    }
}
