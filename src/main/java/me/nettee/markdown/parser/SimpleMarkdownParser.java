package me.nettee.markdown.parser;

import me.nettee.markdown.dom.CodeBlock;
import me.nettee.markdown.dom.Heading;
import me.nettee.markdown.dom.HorizontalRule;
import me.nettee.markdown.dom.Image;
import me.nettee.markdown.dom.ImageParagraph;
import me.nettee.markdown.dom.Line;
import me.nettee.markdown.dom.MarkdownDocument;
import me.nettee.markdown.dom.MarkdownElement;
import me.nettee.markdown.dom.MathBlock;
import me.nettee.markdown.dom.NormalParagraph;
import me.nettee.markdown.dom.Paragraph;
import me.nettee.markdown.dom.Quote;
import me.nettee.markdown.dom.Table;
import me.nettee.markdown.exception.InputDrainedException;
import me.nettee.markdown.exception.ParseMarkdownFailedAtLineException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.nettee.markdown.common.ParserUtils.checkLineState;
import static me.nettee.markdown.common.ParserUtils.checkParserState2;

public class SimpleMarkdownParser implements MarkdownParser {

    private static class ParseElementFailException extends Exception {

        private final Line line;
        private final Class<? extends MarkdownElement> targetClass;

        public ParseElementFailException(Line line, Class<? extends MarkdownElement> targetClass) {
            this.line = line;
            this.targetClass = targetClass;
        }

        public ParseMarkdownFailedAtLineException toMarkdownParseError() {
            return new ParseMarkdownFailedAtLineException(line, targetClass);
        }
    }

    private final Line[] lines;
    private int pos;

    private SimpleMarkdownParser(List<String> lines) {
        int n = lines.size();
        this.lines = new Line[n];
        for (int i = 0; i < n; i++) {
            this.lines[i] = new Line(lines.get(i));
        }
        this.pos = 0;
    }

    private SimpleMarkdownParser(String[] lines) {
        int n = lines.length;
        this.lines = new Line[n];
        for (int i = 0; i < n; i++) {
            this.lines[i] = new Line(lines[i]);
        }
        this.pos = 0;
    }

    private SimpleMarkdownParser(Line[] lines) {
        this.lines = lines;
        this.pos = 0;
    }

    public static SimpleMarkdownParser fromString(String content) {
        return new SimpleMarkdownParser(content.split("\n"));
    }

    public static SimpleMarkdownParser fromFile(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            return new SimpleMarkdownParser(lines);
        } catch (IOException e) {
            throw new InputDrainedException(e);
        }
    }

    public MarkdownDocument parseDocument() {
        List<Paragraph> paragraphs = parseParagraphs();
        if (paragraphs.size() > 0 && paragraphs.get(0) instanceof Heading) {
            Heading heading = (Heading) paragraphs.get(0);
            if (heading.getLevel() == 1) {
                String title = heading.getText();
                return new MarkdownDocument(title, paragraphs.subList(1, paragraphs.size()));
            }
        }
        return new MarkdownDocument(paragraphs);
    }

    public List<Paragraph> parseParagraphs() {
        List<Paragraph> paragraphs = new ArrayList<>();
        while (isNotEof()) {
            consumeWhile(Line::isEmpty);
            if (isNotEof()) {
                paragraphs.add(parseParagraph());
            }
        }
        return paragraphs;
    }

    public Paragraph parseParagraph() {
        Line line = nextLine();
        if (line.isHeading()) {
            return parseHeading();
        } else if (line.isHorizontalRule()) {
            return parseHorizontalRule();
        } else if (line.isQuoted()) {
            return parseQuote();
        } else if (line.isCodeBlockBorder()) {
            return parseCodeBlock();
        } else if (line.isMathBlockBorder()) {
            return parseMathBlock();
        }

        if (line.seemsLikeImage()) {
            Optional<ImageParagraph> imageParagraph = tryParseImageParagraph();
            if (imageParagraph.isPresent()) {
                return imageParagraph.get();
            }
        }

        if (line.seemsLikeTableBorder()) {
            Optional<Table> table = tryParseTable();
            if (table.isPresent()) {
                return table.get();
            }
        }

        return parseNormalParagraph();
    }

    public Heading parseHeading() {
        checkLineState(nextLine(), Line::isHeading, Heading.class);
        Line line = consumeLine();
        return parseHeadingFromLine(line);
    }

    private static Heading parseHeadingFromLine(Line line) {
        Pattern pattern = Pattern.compile("^(#{1,6})\\s+(.+)$");
        Matcher matcher = pattern.matcher(line.getText());
        boolean found = matcher.find();
        checkParserState2(found, new ParseMarkdownFailedAtLineException(line, Heading.class));
        int level = matcher.group(1).length();
        String text = matcher.group(2);
        return new Heading(level, text);
    }

    public HorizontalRule parseHorizontalRule() {
        checkLineState(nextLine(), Line::isHorizontalRule, HorizontalRule.class);
        consumeLine();
        return new HorizontalRule();
    }

    public Quote parseQuote() {
        List<Line> lines = consumeWhile(Line::isQuoted);
        lines.forEach(Line::unindentQuote);
        SimpleMarkdownParser subParser = new SimpleMarkdownParser(lines.toArray(new Line[0]));
        List<Paragraph> paragraphs = subParser.parseParagraphs();
        return new Quote(paragraphs);
    }

    public CodeBlock parseCodeBlock() {
        Line startLine = consumeLine();
        String language = parseCodeLanguageFromLine(startLine);
        List<Line> lines = consumeParagraphUntil(Line::isCodeBlockBorder);
        if (nextLine().isCodeBlockBorder()) {
            consumeLine();
        }
        return new CodeBlock(language, lines2texts(lines));
    }

    private static String parseCodeLanguageFromLine(Line line) {
        Pattern pattern = Pattern.compile("```(\\S*)");
        Matcher matcher = pattern.matcher(line.getText());
        boolean found = matcher.find();
        checkParserState2(found, new ParseMarkdownFailedAtLineException(line, CodeBlock.class));
        return matcher.group(1);
    }

    public MathBlock parseMathBlock() {
        checkLineState(nextLine(), Line::isMathBlockBorder, MathBlock.class);
        consumeLine();
        List<Line> lines = consumeParagraphUntil(Line::isMathBlockBorder);
        if (nextLine().isMathBlockBorder()) {
            consumeLine();
        }
        return new MathBlock(lines2texts(lines));
    }

    private Optional<ImageParagraph> tryParseImageParagraph() {
        List<Line> lines = consumeUntil(Line::isEmpty);

        if (lines.size() == 1) {
            try {
                Image image = tryParseImageFromLine(lines.get(0));
                ImageParagraph imageParagraph = new ImageParagraph(image);
                return Optional.of(imageParagraph);
            } catch (ParseElementFailException e) {
                // do nothing
            }
        }

        return Optional.empty();
    }

    public ImageParagraph parseImageParagraph() {
        Line line = nextLine();
        try {
            Image image = tryParseImageFromLine(line);
            return new ImageParagraph(image);
        } catch (ParseElementFailException e) {
            throw e.toMarkdownParseError();
        }
    }

    private static Image tryParseImageFromLine(Line line) throws ParseElementFailException {
        Pattern pattern = Pattern.compile("^!\\[(.*?)]\\((.+?)\\)$");
        Matcher matcher = pattern.matcher(line.getText());
        boolean found = matcher.find();
        if (!found) {
            throw new ParseElementFailException(line, Image.class);
        }
        String caption = matcher.group(1);
        String uri = matcher.group(2);
        return new Image(caption, uri);
    }

    private Optional<Table> tryParseTable() {
        checkLineState(nextLine(), Line::seemsLikeTableBorder, Table.class);
        List<Line> lines = consumeUntil(Line::isEmpty);

        try {
            Table table = tryParseTableFromLines(lines);
            return Optional.of(table);
        } catch (ParseElementFailException e) {
            // do nothing
        }

        return Optional.empty();
    }

    private static Table tryParseTableFromLines(List<Line> lines) throws ParseElementFailException {
        // TODO
        throw new ParseElementFailException(lines.get(0), Table.class);
    }

    public NormalParagraph parseNormalParagraph() {
        List<Line> lines = consumeUntil(Line::isEmpty);
        return parseNormalParagraphFromLines(lines);
    }

    private static NormalParagraph parseNormalParagraphFromLines(List<Line> lines) {
        return new NormalParagraph(lines2texts(lines));
    }

    private static List<String> lines2texts(List<Line> lines) {
        return lines.stream()
                .map(Line::getText)
                .collect(Collectors.toList());
    }

    private List<Line> consumeParagraphUntil(Predicate<Line> predicate) {
        return consumeUntil(predicate.or(Line::isEmpty));
    }

    private List<Line> consumeParagraphWhile(Predicate<Line> predicate) {
        return consumeWhile(predicate.and(Line::isNotEmpty));
    }

    private List<Line> consumeUntil(Predicate<Line> predicate) {
        return consumeWhile(predicate.negate());
    }

    private List<Line> consumeWhile(Predicate<Line> predicate) {
        List<Line> lines = new ArrayList<>();
        while (isNotEof() && predicate.test(nextLine())) {
            lines.add(consumeLine());
        }
        return lines;
    }

    private Line consumeLine() {
        Line line = lines[pos];
        pos++;
        return line;
    }

    private Line nextLine() {
        return lines[pos];
    }

    private boolean isNotEof() {
        return pos < lines.length;
    }
}
