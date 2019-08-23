package project.exception;

/**
 * This exception is thrown when you try to use an entity that can not be
 * used for the operation.
 * @author Daniel Jurca
 */
public class IllegalEntityException extends RuntimeException {


    public IllegalEntityException() {
    }

    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}

