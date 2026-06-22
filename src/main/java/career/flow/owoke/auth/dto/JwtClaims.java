package career.flow.owoke.auth.dto;

import java.util.List;

public record JwtClaims(
                String id,
                String name,
                String email,
                boolean isEmailVerified,
                List<String> roles) {

}
