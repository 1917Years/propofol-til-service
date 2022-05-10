package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundCommentException extends NoSuchElementException {
    public NotFoundCommentException() {
        super();
    }

    public NotFoundCommentException(String s) {
        super(s);
    }
}
