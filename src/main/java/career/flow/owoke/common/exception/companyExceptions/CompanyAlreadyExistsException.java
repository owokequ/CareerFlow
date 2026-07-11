package career.flow.owoke.common.exception.companyExceptions;

public class CompanyAlreadyExistsException extends RuntimeException {

    public CompanyAlreadyExistsException(String name) {
        super("Company already exists with name: " + name);
    }
}
