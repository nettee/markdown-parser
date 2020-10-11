package me.nettee.markdown.dom;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 解析后的 Markdown 文档对象
 */
@Getter
@ToString
public class MarkdownDocument {

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容
     */
    private List<Paragraph> body;

    /**
     * 文档头（额外内容）
     */
    private List<Paragraph> header;

    /**
     * 文档尾（额外内容）
     */
    private List<Paragraph> footer;

    public MarkdownDocument(List<Paragraph> body) {
        this.body = body;
    }

    public MarkdownDocument(String title, List<Paragraph> body) {
        this.title = title;
        this.body = body;
    }

    public String toDebugString() throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String utf8 = StandardCharsets.UTF_8.name();
        PrintStream out = new PrintStream(baos, true, utf8);
        if (StringUtils.isNoneEmpty(title)) {
            out.print("Title: ");
            out.println(title);
        }
        for (int i = 0; i < body.size(); i++) {
            Paragraph paragraph = body.get(i);
            out.println();
            out.printf("[%d] %s:\n", i, paragraph.getClass().getSimpleName());
            out.println(paragraph.toString());
        }
        return baos.toString(utf8);
    }
}
