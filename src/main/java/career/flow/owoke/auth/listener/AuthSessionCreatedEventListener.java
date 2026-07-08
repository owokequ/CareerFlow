package career.flow.owoke.auth.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import career.flow.owoke.auth.event.AuthSessionCreatedEvent;
import career.flow.owoke.auth.service.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthSessionCreatedEventListener {
    private final RefreshTokenStore refreshTokenStore;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuthSessionCreatedEvent event) {
        refreshTokenStore.saveHash(event.authId(), event.refreshTokenHash());
        log.info("Refresh session created for auth user: {}", event.authId());
    }
}
