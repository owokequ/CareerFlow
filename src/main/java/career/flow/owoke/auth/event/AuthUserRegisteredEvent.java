package career.flow.owoke.auth.event;

public record AuthUserRegisteredEvent(
                String authId,
                String name,
                String email) {

}
