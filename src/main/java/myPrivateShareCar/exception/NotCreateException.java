package myPrivateShareCar.exception;

// REVIEW: добавь d после Create
public class NotCreateException extends RuntimeException {
    public NotCreateException(String message) {
        super(message);
    }
}
