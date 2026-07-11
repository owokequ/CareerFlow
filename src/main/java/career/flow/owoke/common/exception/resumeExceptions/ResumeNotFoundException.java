package career.flow.owoke.common.exception.resumeExceptions;

public class ResumeNotFoundException extends RuntimeException {

    public ResumeNotFoundException(String id) {
        super("Resume not found with id: " + id);
    }
}
