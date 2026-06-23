package career.flow.owoke.common.exception.userExceptions;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException(String token) {
        super("Invalid verification token: " + token);
    }
}
