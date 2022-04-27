package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundBoard extends NoSuchElementException {
    public NotFoundBoard() {
        super();
    }

    public NotFoundBoard(String s) {
        super(s);
    }
}
