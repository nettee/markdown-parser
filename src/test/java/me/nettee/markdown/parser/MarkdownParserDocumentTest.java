package me.nettee.markdown.parser;

import com.google.common.io.Resources;
import lombok.Getter;
import me.nettee.markdown.dom.MarkdownDocument;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

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

    private static class MarkdownFileVisitor extends SimpleFileVisitor<Path> {

        @Getter
        private final List<Path> markdownFiles = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            FileVisitResult res = super.visitFile(file, attrs);
            if (Files.isDirectory(file)) {
                return res;
            }
            if (file.toFile().getName().endsWith(".md")) {
                markdownFiles.add(file);
            }
            return res;
        }
    }

    @Test
    public void test1() throws IOException {
        Path path = Paths.get("/Users/william/bloomstore/LeetCode 例题精讲");
        MarkdownFileVisitor visitor = new MarkdownFileVisitor();
        Files.walkFileTree(path, visitor);
        List<Path> markdownFiles = visitor.getMarkdownFiles();
        for (Path markdownFile : markdownFiles) {
            System.out.println("markdownFile = " + markdownFile);
            SimpleMarkdownParser parser = SimpleMarkdownParser.fromFile(markdownFile);
            MarkdownDocument document = parser.parseDocument();
            document.toDebugString();
        }
    }
}
