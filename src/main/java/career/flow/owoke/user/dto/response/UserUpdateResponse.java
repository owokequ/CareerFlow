package career.flow.owoke.user.dto.response;

public record UserUpdateResponse(
        String id,
        String authId,
        String name,
        String email
) {
}
