package me.nettee.markdown.model;

import org.apache.commons.lang3.StringUtils;

public class Line {

    /**
     * 文本
     */
    private String text;

    public boolean isEmpty() {
        return StringUtils.isEmpty(text);
    }

    public boolean isHeading() {
        return StringUtils.startsWith(text, "#");
    }

    public boolean isHorizontalRule() {
        return StringUtils.equalsAny(text, "---", "***");
    }

    public boolean isQuoted() {
        return StringUtils.startsWith(text, ">");
    }

    public boolean isMathBlockBorder() {
        return StringUtils.startsWith(text, "$$");
    }

    public boolean isCodeBlockBorder() {
        return StringUtils.startsWith(text, "```");
    }
}
