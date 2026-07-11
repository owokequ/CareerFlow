package career.flow.owoke.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyCreateRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 200, message = "Name must be at most 200 characters")
        String name,

        @Size(max = 500, message = "Website must be at most 500 characters")
        String website,

        @Size(max = 150, message = "Industry must be at most 150 characters")
        String industry,

        @Size(max = 200, message = "Location must be at most 200 characters")
        String location,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description) {
}
