package me.nettee.markdown.parser;

import me.nettee.markdown.model.CodeBlock;
import me.nettee.markdown.model.Heading;
import me.nettee.markdown.model.HorizontalRule;
import me.nettee.markdown.model.Image;
import me.nettee.markdown.model.ImageParagraph;
import me.nettee.markdown.model.MarkdownDocument;
import me.nettee.markdown.model.MathBlock;
import me.nettee.markdown.model.NormalParagraph;
import me.nettee.markdown.model.Paragraph;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class SimpleMarkdownParserTest {

    @Test(expected = UnsupportedOperationException.class)
    public void constructParser() {
        SimpleMarkdownParser parser = SimpleMarkdownParser.fromString("");
        MarkdownDocument document = parser.parse();
    }

    @Test
    public void parseDocument() {
    }

    private void testParseParagraph(Paragraph paragraph, Function<SimpleMarkdownParser, Paragraph> f) {
        String paragraphString = paragraph.toString();
        SimpleMarkdownParser parser = SimpleMarkdownParser.fromString(paragraphString);
        assertEquals(paragraph, f.apply(parser));
    }

    @Test
    public void parseHeading() {
        Heading heading = new Heading(3, "this is a heading");
        testParseParagraph(heading, SimpleMarkdownParser::parseHeading);
    }

    @Test
    public void parseHorizontalRule() {
        HorizontalRule horizontalRule = new HorizontalRule();
        testParseParagraph(horizontalRule, SimpleMarkdownParser::parseHorizontalRule);
    }

    @Test
    public void parseImageParagraph() {
        ImageParagraph imageParagraph = new ImageParagraph(new Image("some caption", "some/uri.jpg"));
        testParseParagraph(imageParagraph, SimpleMarkdownParser::parseImageParagraph);
    }

    @Test
    public void parseQuote() {
        // TODO
    }

    @Test
    public void parseCodeBlock() {
        CodeBlock codeBlock = new CodeBlock("Java", Arrays.asList("line1", "line2", "line3"));
        testParseParagraph(codeBlock, SimpleMarkdownParser::parseCodeBlock);
    }

    @Test
    public void parseMathBlock() {
        MathBlock mathBlock = new MathBlock(Arrays.asList("line1", "line2"));
        testParseParagraph(mathBlock, SimpleMarkdownParser::parseMathBlock);
    }

    @Test
    public void parseNormalParagraph() {
        NormalParagraph normalParagraph = new NormalParagraph(Arrays.asList("line1", "line2"));
        testParseParagraph(normalParagraph, SimpleMarkdownParser::parseNormalParagraph);
    }

}
