package career.flow.owoke.auth.event;

public record PasswordResetRequestedEvent(
        String authId,
        String email) {

}
