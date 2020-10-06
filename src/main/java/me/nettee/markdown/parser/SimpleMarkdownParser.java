package me.nettee.markdown.parser;

import me.nettee.markdown.dom.CodeBlock;
import me.nettee.markdown.dom.Heading;
import me.nettee.markdown.dom.HorizontalRule;
import me.nettee.markdown.dom.Image;
import me.nettee.markdown.dom.ImageParagraph;
import me.nettee.markdown.dom.Line;
import me.nettee.markdown.dom.MarkdownDocument;
import me.nettee.markdown.dom.MathBlock;
import me.nettee.markdown.dom.NormalParagraph;
import me.nettee.markdown.dom.Paragraph;
import me.nettee.markdown.dom.Quote;
import me.nettee.markdown.dom.Table;
import me.nettee.markdown.model.FallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class SimpleMarkdownParser implements MarkdownParser {

    private final Line[] lines;
    private int pos;

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

    public MarkdownDocument parseDocument() {
        List<Paragraph> paragraphs = parseParagraphs();
        if (paragraphs.size() > 0 && paragraphs.get(0) instanceof Heading) {
            Heading heading = (Heading) paragraphs.get(0);
            checkState(heading.getLevel() == 1);
            String title = heading.getText();
            return new MarkdownDocument(title, paragraphs.subList(1, paragraphs.size()));
        } else {
            return new MarkdownDocument(paragraphs);
        }
    }

    private List<Paragraph> parseParagraphs() {
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
        } else if (line.isQuoted()) {
            return parseQuote();
        } else if (line.isCodeBlockBorder()) {
            return parseCodeBlock();
        } else if (line.isMathBlockBorder()) {
            return parseMathBlock();
        } else if (line.seemsLikeImage()) {
            return tryParseImageParagraph().nullSafeGet();
        } else if (line.seemsLikeTableBorder()) {
            return tryParseTable().nullSafeGet();
        } else {
            return parseNormalParagraph();
        }
    }

    Heading parseHeading() {
        checkState(nextLine().isHeading());
        Line line = consumeLine();
        return parseHeadingFromLine(line);
    }

    private static Heading parseHeadingFromLine(Line line) {
        Pattern pattern = Pattern.compile("^(#{1,6})\\s+(.+)$");
        Matcher matcher = pattern.matcher(line.getText());
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

    Quote parseQuote() {
        List<Line> lines = consumeWhile(Line::isQuoted);
        lines.forEach(Line::unindentQuote);
        SimpleMarkdownParser subParser = new SimpleMarkdownParser(lines.toArray(new Line[0]));
        List<Paragraph> paragraphs = subParser.parseParagraphs();
        return new Quote(paragraphs);
    }

    CodeBlock parseCodeBlock() {
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
        checkState(found);
        return matcher.group(1);
    }

    MathBlock parseMathBlock() {
        checkState(nextLine().isMathBlockBorder());
        consumeLine();
        List<Line> lines = consumeParagraphUntil(Line::isMathBlockBorder);
        if (nextLine().isMathBlockBorder()) {
            consumeLine();
        }
        return new MathBlock(lines2texts(lines));
    }

    private FallBack<Paragraph, ImageParagraph, NormalParagraph> tryParseImageParagraph() {
        checkState(nextLine().seemsLikeImage());
        List<Line> lines = consumeUntil(Line::isEmpty);
        // 首先尝试 parse 成图片
        if (lines.size() == 1) {
            Optional<Image> image = tryParseImageFromLine(lines.get(0));
            if (image.isPresent()) {
                return FallBack.primary(new ImageParagraph(image.get()));
            }
        }
        // 如果 parse 失败则作为普通段落处理
        NormalParagraph normalParagraph = parseNormalParagraphFromLines(lines);
        return FallBack.secondary(normalParagraph);
    }

    ImageParagraph parseImageParagraph() {
        Optional<Image> image = tryParseImageFromLine(nextLine());
        checkState(image.isPresent());
        return new ImageParagraph(image.get());
    }

    private static Optional<Image> tryParseImageFromLine(Line line) {
        Pattern pattern = Pattern.compile("^!\\[(.*?)]\\((.+?)\\)$");
        Matcher matcher = pattern.matcher(line.getText());
        boolean found = matcher.find();
        if (!found) {
            return Optional.empty();
        }
        String caption = matcher.group(1);
        String uri = matcher.group(2);
        return Optional.of(new Image(caption, uri));
    }

    private FallBack<Paragraph, Table, NormalParagraph> tryParseTable() {
        checkState(nextLine().seemsLikeTableBorder());
        List<Line> lines = consumeUntil(Line::isEmpty);
        // 首先尝试 parse 成表格
        Optional<Table> table = tryParseTableFromLines(lines);
        if (table.isPresent()) {
            return FallBack.primary(table.get());
        }
        // 如果 parse 失败则作为普通段落处理
        NormalParagraph normalParagraph = parseNormalParagraphFromLines(lines);
        return FallBack.secondary(normalParagraph);
    }

    private static Optional<Table> tryParseTableFromLines(List<Line> lines) {
        // TODO;
        return Optional.empty();
    }

    NormalParagraph parseNormalParagraph() {
        List<Line> lines = consumeUntil(Line::isEmpty);
        return parseNormalParagraphFromLines(lines);
    }

    private static NormalParagraph parseNormalParagraphFromLines(List<Line> lines) {
        return new NormalParagraph(lines2texts(lines));
    }

    private static List<String> lines2texts(List<Line> lines) {
        return lines.stream()
                .map(Line::getText)
                .collect(toList());
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
