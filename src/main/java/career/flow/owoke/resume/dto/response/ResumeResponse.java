package career.flow.owoke.resume.dto.response;

import java.time.Instant;

public record ResumeResponse(
        String id,
        String userId,
        String title,
        String targetPosition,
        String skills,
        String summary,
        Instant createdAt,
        Instant updatedAt) {

}
