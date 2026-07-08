package career.flow.owoke.messaging.event;

public record AuthUserCreatedEvent(
        String authId,
        String name,
        String email) {

}
