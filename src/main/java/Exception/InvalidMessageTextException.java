package Exception;

public class InvalidMessageTextException extends Exception {

    public InvalidMessageTextException() {
        super();
    }

    public InvalidMessageTextException(String s) {
        super(s);
    }

    public InvalidMessageTextException(Throwable cause) {
        super(cause);
    }

    public InvalidMessageTextException(String s, Throwable cause) {
        super(s, cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
