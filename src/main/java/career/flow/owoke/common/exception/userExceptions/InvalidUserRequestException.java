package career.flow.owoke.common.exception.userExceptions;

public class InvalidUserRequestException extends RuntimeException {
    public InvalidUserRequestException(String message) {
        super("Invalid user request: " + message);
    }
}
