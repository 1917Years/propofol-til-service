package propofol.tilservice.api.common.exception;

public class ImageCreateException extends RuntimeException{
    public ImageCreateException() {
        super();
    }

    public ImageCreateException(String message) {
        super(message);
    }

    public ImageCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageCreateException(Throwable cause) {
        super(cause);
    }

    protected ImageCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
