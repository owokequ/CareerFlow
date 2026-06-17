package career.flow.owoke.user.dto.response;

public record UserResponse(
        String id,
        String authId,
        String name,
        String email
) {
}
