package Exception;

public class InvalidNewAccountInputException extends Exception {

    public InvalidNewAccountInputException() {
        super();
    }

    public InvalidNewAccountInputException(String s) {
        super(s);
    }

    public InvalidNewAccountInputException(Throwable cause) {
        super(cause);
    }

    public InvalidNewAccountInputException(String s, Throwable cause) {
        super(s, cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
