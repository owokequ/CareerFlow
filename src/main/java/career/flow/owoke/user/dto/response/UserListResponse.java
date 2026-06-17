package career.flow.owoke.user.dto.response;

import java.util.List;

public record UserListResponse(
        List<UserResponse> users
) {
}
