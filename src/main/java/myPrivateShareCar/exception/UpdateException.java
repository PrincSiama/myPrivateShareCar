package myPrivateShareCar.exception;

public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }
    public UpdateException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
