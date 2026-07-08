package career.flow.owoke.auth.listener;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import career.flow.owoke.auth.event.PasswordResetRequestedEvent;
import career.flow.owoke.auth.service.RedisService;
import career.flow.owoke.messaging.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetRequestedEventListener {

    private final RedisService redisService;
    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(PasswordResetRequestedEvent event) {
        String token = UUID.randomUUID().toString();
        redisService.save("reset:" + token, event.authId(), Duration.ofMinutes(10));
        emailService.sendForgotPasswordEmail(event.email(), token);
        log.info("Password reset email requested for auth user: {}", event.authId());
    }
}
