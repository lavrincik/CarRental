package project.exception;

/**
 * This exception is thrown when validation of entity fails.
 * @author Daniel Jurca
 */
public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String msg) {
        super(msg);
    }
}

