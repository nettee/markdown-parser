package me.nettee.markdown.parser;

import me.nettee.markdown.model.CodeBlock;
import me.nettee.markdown.model.Heading;
import me.nettee.markdown.model.HorizontalRule;
import me.nettee.markdown.model.Image;
import me.nettee.markdown.model.ImageParagraph;
import me.nettee.markdown.model.Line;
import me.nettee.markdown.model.MarkdownDocument;
import me.nettee.markdown.model.MathBlock;
import me.nettee.markdown.model.NormalParagraph;
import me.nettee.markdown.model.Paragraph;
import me.nettee.markdown.model.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

public class SimpleMarkdownParser implements MarkdownParser {

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
        List<Paragraph> paragraphs = parsePargraphs();
        if (paragraphs.size() > 0 && paragraphs.get(0) instanceof Heading) {
            Heading heading = (Heading) paragraphs.get(0);
            checkState(heading.getLevel() == 1);
            String title = heading.getText();
            return new MarkdownDocument(title, paragraphs.subList(1, paragraphs.size()));
        } else {
            return new MarkdownDocument(paragraphs);
        }
    }

    private List<Paragraph> parsePargraphs() {
        List<Paragraph> paragraphs = new ArrayList<>();
        while (isNotEof()) {
            Line line = nextLine();
            if (line.isEmpty()) {
                consumeLine();
            } else {
                paragraphs.add(parseParagraph());
            }
        }
        return paragraphs;
    }

    private Paragraph parseParagraph() {
        Line line = nextLine();
        if (line.isHeading()) {
            return parseHeading();
        } else if (line.isHorizontalRule()) {
            return parseHorizontalRule();
        } else if (line.isImage()) {
            return parseImageParagraph();
        } else if (line.isQuoted()) {
            return parseQuote();
        } else if (line.isCodeBlockBorder()) {
            return parseCodeBlock();
        } else if (line.isMathBlockBorder()) {
            return parseMathBlock();
        } else {
            return parseNormalParagraph();
        }
    }

    Heading parseHeading() {
        checkState(nextLine().isHeading());
        Line line = consumeLine();
        return parseHeadingFromLine(line.getText());
    }

    private static Heading parseHeadingFromLine(String line) {
        Pattern pattern = Pattern.compile("^(#{1,6})\\s+(.+)$");
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        checkState(found);
        int level = matcher.group(1).length();
        String text = matcher.group(2);
        return new Heading(level, text);
    }

    HorizontalRule parseHorizontalRule() {
        checkState(nextLine().isHorizontalRule());
        consumeLine();
        return new HorizontalRule();
    }

    ImageParagraph parseImageParagraph() {
        checkState(nextLine().isImage());
        Line line = consumeLine();
        return parseImageFromLine(line.getText());
    }

    private static ImageParagraph parseImageFromLine(String line) {
        Pattern pattern = Pattern.compile("^!\\[(.*?)]\\((.+?)\\)$");
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        checkState(found);
        String caption = matcher.group(1);
        String uri = matcher.group(2);
        return new ImageParagraph(new Image(caption, uri));
    }

    Quote parseQuote() {
        // TODO
        throw new UnsupportedOperationException();
    }

    CodeBlock parseCodeBlock() {
        Line startLine = consumeLine();
        String language = parseCodeLanguageFromLine(startLine.getText());
        List<Line> lines = consumeWhile(line -> !line.isCodeBlockBorder());
        consumeLine();
        return new CodeBlock(language, lines2texts(lines));
    }

    private static String parseCodeLanguageFromLine(String line) {
        Pattern pattern = Pattern.compile("```(\\S*)");
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        checkState(found);
        return matcher.group(1);
    }

    MathBlock parseMathBlock() {
        consumeLine();
        List<Line> lines = consumeWhile(line -> !line.isMathBlockBorder());
        consumeLine();
        return new MathBlock(lines2texts(lines));
    }

    NormalParagraph parseNormalParagraph() {
        List<Line> lines = consumeWhile(line -> !line.isEmpty());
        return new NormalParagraph(lines2texts(lines));
    }

    private List<String> lines2texts(List<Line> lines) {
        return lines.stream()
                .map(Line::getText)
                .collect(Collectors.toList());
    }

    private List<Line> consumeWhile(Predicate<Line> predicate) {
        List<Line> lines = new ArrayList<>();
        while (isNotEof() && predicate.test(nextLine())) {
            lines.add(consumeLine());
        }
        return lines;
    }

    private Line consumeLine() {
        Line line = new Line(lines[pos]);
        pos++;
        return line;
    }

    private Line nextLine() {
        return new Line(lines[pos]);
    }

    private boolean isNotEof() {
        return pos < lines.length;
    }
}
