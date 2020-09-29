package me.nettee.markdown.model;

import java.util.List;

/**
 * 代码块
 */
public class CodeBlock extends Paragraph {

    /**
     * 代码语言
     */
    private String language;

    /**
     * 代码内容
     */
    private List<String> lines;
}
