package me.nettee.markdown.common;

import me.nettee.markdown.dom.Line;
import me.nettee.markdown.dom.MarkdownElement;
import me.nettee.markdown.exception.ParseMarkdownFailedAtLineException;
import me.nettee.markdown.exception.MarkdownParserException;
import me.nettee.markdown.exception.ParseMarkdownFailedException;

import java.util.function.Predicate;

public class ParserUtils {

    public static void checkParserState(boolean expression, String messageTemplate, Object... args) {
        if (!expression) {
            String message = String.format(messageTemplate, args);
            throw new ParseMarkdownFailedException(message);
        }
    }

    public static void checkLineState(Line line, Predicate<Line> linePredicate, Class<? extends MarkdownElement> targetClass) {
        checkParserState2(linePredicate.test(line), new ParseMarkdownFailedAtLineException(line, targetClass));
    }

    public static void checkParserState2(boolean expression, MarkdownParserException e) {
        if (!expression) {
            throw e;
        }
    }
}
