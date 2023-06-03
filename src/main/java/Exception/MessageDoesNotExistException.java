package Exception;

public class MessageDoesNotExistException extends Exception {

    public MessageDoesNotExistException() {
        super();
    }

    public MessageDoesNotExistException(String s) {
        super(s);
    }

    public MessageDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public MessageDoesNotExistException(String s, Throwable cause) {
        super(s, cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
