package cz.mendelu.dnaAnalyser.sequence.stream;

public class WindowStreamException extends RuntimeException {

    public WindowStreamException(String message) {
        super(message);
    }

    public WindowStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
