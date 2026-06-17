package career.flow.owoke.user.dto.response;

public record UserCreateResponse(
        String id,
        String authId,
        String name,
        String email
) {
}
