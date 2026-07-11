package career.flow.owoke.common.exception.companyExceptions;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(String id) {
        super("Company not found with id: " + id);
    }
}
