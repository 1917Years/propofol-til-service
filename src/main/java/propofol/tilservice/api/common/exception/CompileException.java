package propofol.tilservice.api.common.exception;

public class CompileException extends IllegalStateException{
    public CompileException() {
        super();
    }

    public CompileException(String s) {
        super(s);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileException(Throwable cause) {
        super(cause);
    }
}
