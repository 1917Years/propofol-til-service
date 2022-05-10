package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundBoardException extends NoSuchElementException {
    public NotFoundBoardException() {
        super();
    }

    public NotFoundBoardException(String s) {
        super(s);
    }
}
