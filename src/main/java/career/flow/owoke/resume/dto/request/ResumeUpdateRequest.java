package career.flow.owoke.resume.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResumeUpdateRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @NotBlank(message = "Target position cannot be blank")
        @Size(max = 150, message = "Target position must be at most 150 characters")
        String targetPosition,

        @NotBlank(message = "Skills cannot be blank")
        @Size(max = 1000, message = "Skills must be at most 1000 characters")
        String skills,

        @Size(max = 2000, message = "Summary must be at most 2000 characters")
        String summary) {

}
