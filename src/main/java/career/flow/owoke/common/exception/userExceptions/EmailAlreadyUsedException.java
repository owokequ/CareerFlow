package career.flow.owoke.common.exception.userExceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Email already exists: " + email);
    }
}
