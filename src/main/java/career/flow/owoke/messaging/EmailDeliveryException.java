package career.flow.owoke.messaging;

public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String email, Throwable cause) {
        super("Failed to send email to " + email, cause);
    }

}
