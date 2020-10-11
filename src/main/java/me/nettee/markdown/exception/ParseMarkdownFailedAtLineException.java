package me.nettee.markdown.exception;

import lombok.Getter;
import me.nettee.markdown.dom.Line;
import me.nettee.markdown.dom.MarkdownElement;

@Getter
public class ParseMarkdownFailedAtLineException extends MarkdownParserException {

    private final Line line;
    private final Class<? extends MarkdownElement> targetClass;

    public ParseMarkdownFailedAtLineException(Line line, Class<? extends MarkdownElement> targetClass) {
        super(getMessageFromLineAndTargetClass(line, targetClass));
        this.line = line;
        this.targetClass = targetClass;
    }

    private static String getMessageFromLineAndTargetClass(Line line, Class<? extends MarkdownElement> targetClass) {
        return String.format("failed to parse line as %s. line: `%s'", targetClass.getSimpleName(), line.getText());
    }
}
