package propofol.tilservice.api.common.exception;

public class ImageBoardChangeException extends RuntimeException{
    public ImageBoardChangeException() {
        super();
    }

    public ImageBoardChangeException(String message) {
        super(message);
    }

    public ImageBoardChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageBoardChangeException(Throwable cause) {
        super(cause);
    }

    protected ImageBoardChangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
