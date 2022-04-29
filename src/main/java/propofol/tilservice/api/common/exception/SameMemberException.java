package propofol.tilservice.api.common.exception;

public class SameMemberException extends RuntimeException{
    public SameMemberException() {
        super();
    }

    public SameMemberException(String message) {
        super(message);
    }

    public SameMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameMemberException(Throwable cause) {
        super(cause);
    }

    protected SameMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
