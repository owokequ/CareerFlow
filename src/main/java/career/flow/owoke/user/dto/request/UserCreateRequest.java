package career.flow.owoke.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Authentication ID cannot be blank") String authenticationId,

        @NotBlank(message = "Name cannot be blank") @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters") String name,

        @NotBlank(message = "Email cannot be blank") @Email(message = "Email must be valid") @Size(max = 255, message = "Email must be between 1 and 255 characters") String email) {
}
