package me.nettee.markdown.model;

import lombok.Getter;
import lombok.ToString;

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

}
