package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundFileException extends NoSuchElementException {
    public NotFoundFileException() {
        super();
    }

    public NotFoundFileException(String s) {
        super(s);
    }
}
