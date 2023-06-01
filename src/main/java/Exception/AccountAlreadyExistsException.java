package Exception;

public class AccountAlreadyExistsException extends Exception {

    public AccountAlreadyExistsException() {
        super();
    }

    public AccountAlreadyExistsException(String s) {
        super(s);
    }

    public AccountAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public AccountAlreadyExistsException(String s, Throwable cause) {
        super(s, cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
