package me.nettee.markdown.exception;

import lombok.Getter;
import me.nettee.markdown.dom.Line;
import me.nettee.markdown.dom.MarkdownElement;

@Getter
public class MarkdownParseError extends RuntimeException {

    private final Line line;
    private final Class<? extends MarkdownElement> targetClass;

    public MarkdownParseError(Line line, Class<? extends MarkdownElement> targetClass) {
        this.line = line;
        this.targetClass = targetClass;
    }

    @Override
    public String getMessage() {
        return String.format("failed to parse line as %s. line: `%s'", targetClass.getSimpleName(), line.getText());
    }
}
