package myPrivateShareCar.exception;

// REVIEW: я бы единообразно с исключением на создание назвала. Либо NotUpdatedException здесь, либо CreateException там
public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }
    public UpdateException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
