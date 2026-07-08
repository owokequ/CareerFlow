package career.flow.owoke.auth.listener;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import career.flow.owoke.auth.event.AuthUserRegisteredEvent;
import career.flow.owoke.messaging.event.AuthUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthUserRegisteredEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuthUserRegisteredEvent event) {
        log.info("Publishing auth user created event to Kafka");

        kafkaTemplate.send("auth", new AuthUserCreatedEvent(
                event.authId(),
                event.name(),
                event.email()));
    }
}
