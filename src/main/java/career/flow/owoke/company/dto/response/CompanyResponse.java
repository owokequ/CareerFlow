package career.flow.owoke.company.dto.response;

import java.time.Instant;

public record CompanyResponse(
        String id,
        String userId,
        String name,
        String website,
        String industry,
        String location,
        String description,
        Instant createdAt,
        Instant updatedAt) {
}
