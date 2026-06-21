package career.flow.owoke.auth.dto.response;

public record AuthUserResponse(
        String accessToken,
        String refreshToken) {

}
