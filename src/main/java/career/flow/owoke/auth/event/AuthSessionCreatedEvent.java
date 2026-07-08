package career.flow.owoke.auth.event;

public record AuthSessionCreatedEvent(
        String authId,
        String refreshTokenHash) {

}
