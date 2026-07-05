package career.flow.owoke.common.exception.authExceptions;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String userId) {
        super("Refresh token not found for user id: " + userId);
    }
}
