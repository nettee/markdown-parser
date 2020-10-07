package me.nettee.markdown.dom;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Line {

    /**
     * 文本
     */
    private String text;

    public Line(String text) {
        this.text = text;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(text);
    }

    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(text);
    }

    public boolean isHeading() {
        return StringUtils.startsWith(text, "#");
    }

    public boolean isHorizontalRule() {
        // TODO regex
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

    public boolean seemsLikeImage() {
        return StringUtils.startsWith(text, "!");
    }

    public boolean seemsLikeTableBorder() {
        return StringUtils.startsWith(text, "|")
                && StringUtils.endsWith(text, "|");
    }

    public void unindentQuote() {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        if (text.startsWith("> ")) {
            text = text.substring(2);
        } else if (text.startsWith(">")) {
            text = text.substring(1);
        }
    }
}
