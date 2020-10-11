package me.nettee.markdown.exception;

public abstract class MarkdownParserException extends RuntimeException {

    protected MarkdownParserException() {
        super();
    }

    protected MarkdownParserException(String message) {
        super(message);
    }

    protected MarkdownParserException(String message, Throwable cause) {
        super(message, cause);
    }

    protected MarkdownParserException(Throwable cause) {
        super(cause);
    }
}
