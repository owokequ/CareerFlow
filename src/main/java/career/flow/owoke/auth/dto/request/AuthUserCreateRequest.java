package career.flow.owoke.auth.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthUserCreateRequest(

        @NotBlank(message = "Name cannot be blank") @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters") String name,

        @NotBlank(message = "Email cannot be blank") @Email(message = "Email must be valid") @Size(max = 255, message = "Email must be between 1 and 255 characters") String email,

        @NotNull(message = "Password cannot be null") @Length(min = 8, max = 255, message = "Password must be between 8 and 255 characters") String password

) {

}
