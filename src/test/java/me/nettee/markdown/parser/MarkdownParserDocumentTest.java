package me.nettee.markdown.parser;

import com.google.common.io.Resources;
import me.nettee.markdown.dom.MarkdownDocument;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MarkdownParserDocumentTest {

    @SuppressWarnings("UnstableApiUsage")
    @Test
    public void parseDocument() throws IOException {
        URL url = Resources.getResource("markdown/markdown-sample.md");
        String content = Resources.toString(url, StandardCharsets.UTF_8);
        SimpleMarkdownParser parser = SimpleMarkdownParser.fromString(content);
        MarkdownDocument document = parser.parseDocument();
        System.out.println(document.toDebugString());
    }
}
