package Exception;

public class AccountDoesNotExistException extends Exception {

    public AccountDoesNotExistException() {
        super();
    }

    public AccountDoesNotExistException(String s) {
        super(s);
    }

    public AccountDoesNotExistException(Throwable cause) {
        super(cause);
    }

    public AccountDoesNotExistException(String s, Throwable cause) {
        super(s, cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
