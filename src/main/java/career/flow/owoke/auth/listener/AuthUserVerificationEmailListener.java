package career.flow.owoke.auth.listener;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import career.flow.owoke.auth.event.AuthUserRegisteredEvent;
import career.flow.owoke.auth.service.RedisService;
import career.flow.owoke.messaging.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthUserVerificationEmailListener {
    private final EmailService emailService;
    private final RedisService redisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuthUserRegisteredEvent event) {
        String token = UUID.randomUUID().toString();

        redisService.save("verify:" + token, event.authId(), Duration.ofMinutes(10));
        emailService.sendVerificationEmail(event.email(), token);
        log.info("Verification email requested for auth user: {}", event.authId());

    }

}
