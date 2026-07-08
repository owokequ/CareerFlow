package career.flow.owoke.auth.listener;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import career.flow.owoke.auth.event.AuthSessionCreatedEvent;
import career.flow.owoke.auth.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthSessionCreatedEventListener {
    private final RedisService redisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuthSessionCreatedEvent event) {
        redisService.save("refresh:" + event.authId(), event.refreshTokenHash(), Duration.ofDays(30));
        log.info("Refresh session created for auth user: {}", event.authId());
    }
}
