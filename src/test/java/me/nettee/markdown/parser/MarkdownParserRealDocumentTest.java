package me.nettee.markdown.parser;

import lombok.Getter;
import me.nettee.markdown.dom.MarkdownDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class MarkdownParserRealDocumentTest {

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

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IOException {
        Path rootPath = Paths.get("/Users/william/bloomstore/LeetCode 例题精讲");
        MarkdownFileVisitor visitor = new MarkdownFileVisitor();
        Files.walkFileTree(rootPath, visitor);
        List<Path> markdownFiles = visitor.getMarkdownFiles();
        return markdownFiles.stream()
                .map(path -> new Object[]{path.getFileName().toString(), path})
                .collect(Collectors.toList());
    }

    @Parameter(0)
    public String fileName;

    @Parameter(1)
    public Path filePath;

    @Test
    public void testParse() throws UnsupportedEncodingException {
        SimpleMarkdownParser parser = SimpleMarkdownParser.fromFile(filePath);
        MarkdownDocument document = parser.parseDocument();
        System.out.println(document.toDebugString());
    }

}
