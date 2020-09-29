package me.nettee.markdown.parser;

import me.nettee.markdown.model.MarkdownDocument;
import org.junit.Test;

public class SimpleMarkdownParserTest {

    @Test(expected = UnsupportedOperationException.class)
    public void constructParser() {
        SimpleMarkdownParser parser = SimpleMarkdownParser.fromString("");
        MarkdownDocument document = parser.parse();
    }

}
