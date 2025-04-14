package development.cloudstorageproject.exception;

public class InvalidUserAlreadyExists extends RuntimeException {
    public InvalidUserAlreadyExists(String message) {
        super(message);
    }
}
