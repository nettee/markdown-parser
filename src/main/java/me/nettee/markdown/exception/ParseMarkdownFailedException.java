package me.nettee.markdown.exception;

public class ParseMarkdownFailedException extends MarkdownParserException {

    public ParseMarkdownFailedException() {
        super();
    }

    public ParseMarkdownFailedException(String message) {
        super(message);
    }

    public ParseMarkdownFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseMarkdownFailedException(Throwable cause) {
        super(cause);
    }
}
