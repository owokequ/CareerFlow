package career.flow.owoke.auth.dto.response;

public record JwtCreateResponse(
                String authenticationId,

                String name,

                String email) {
}
