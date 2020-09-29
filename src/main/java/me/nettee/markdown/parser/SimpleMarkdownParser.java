package me.nettee.markdown.parser;

import me.nettee.markdown.model.MarkdownDocument;

public class SimpleMarkdownParser implements MarkdownParser {

    private SimpleMarkdownParser() {}

    private SimpleMarkdownParser(String[] lines) {
        this.lines = lines;
        this.pos = 0;
    }

    private String[] lines;
    private int pos;

    public static SimpleMarkdownParser fromString(String content) {
        return new SimpleMarkdownParser(content.split("\n"));
    }

    public MarkdownDocument parse() {
        throw new UnsupportedOperationException();
    }
}
