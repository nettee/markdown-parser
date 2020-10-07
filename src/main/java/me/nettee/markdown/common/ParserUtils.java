package me.nettee.markdown.common;

import me.nettee.markdown.exception.MarkdownParserException;

public class ParserUtils {

    public static void checkParserState(boolean expression, MarkdownParserException e) {
        if (!expression) {
            throw e;
        }
    }
}
