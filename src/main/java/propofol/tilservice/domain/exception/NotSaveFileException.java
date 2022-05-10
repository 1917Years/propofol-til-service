package propofol.tilservice.domain.exception;

import java.io.IOException;

public class NotSaveFileException extends IOException {
    public NotSaveFileException() {
        super();
    }

    public NotSaveFileException(String message) {
        super(message);
    }

    public NotSaveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSaveFileException(Throwable cause) {
        super(cause);
    }
}
