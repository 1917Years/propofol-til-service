package propofol.tilservice.api.common.exception;

public class BoardUpdateException extends RuntimeException{
    public BoardUpdateException() {
        super();
    }

    public BoardUpdateException(String message) {
        super(message);
    }

    public BoardUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardUpdateException(Throwable cause) {
        super(cause);
    }

    protected BoardUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
