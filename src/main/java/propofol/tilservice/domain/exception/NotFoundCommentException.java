package propofol.tilservice.domain.exception;

import propofol.tilservice.api.common.exception.ImageBoardChangeException;

import java.util.NoSuchElementException;

public class NotFoundCommentException extends ImageBoardChangeException {
    public NotFoundCommentException() {
        super();
    }

    public NotFoundCommentException(String s) {
        super(s);
    }
}
