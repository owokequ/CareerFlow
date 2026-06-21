package career.flow.owoke.auth.dto.request;

public record AuthUserLoginRequest(
        String email,
        String password) {

}
