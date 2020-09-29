package me.nettee.markdown.model;

/**
 * 标题
 */
public class Heading extends Paragraph {

    /**
     * 标题级别，取值范围 1~6
     */
    private int level;

    /**
     * 标题文本
     */
    private String text;
}
