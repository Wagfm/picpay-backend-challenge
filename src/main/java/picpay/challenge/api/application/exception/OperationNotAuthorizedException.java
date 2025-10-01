package picpay.challenge.api.application.exception;

public class OperationNotAuthorizedException extends RuntimeException {
    public OperationNotAuthorizedException(String message) {
        super(message);
    }
}
