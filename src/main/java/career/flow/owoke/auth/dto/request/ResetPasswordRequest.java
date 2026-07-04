package career.flow.owoke.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
                @NotBlank(message = "Password cannot be blank")
                @Length(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
                String newPassword) {

}
