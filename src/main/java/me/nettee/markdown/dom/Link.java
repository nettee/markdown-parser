package me.nettee.markdown.dom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 链接
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class Link extends InlineElement {

    /**
     * 链接文本
     */
    private final String text;

    /**
     * 链接 URI
     */
    private final String uri;

    public Link(String text, String uri) {
        this.text = text;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return String.format("[%s](%s)", text, uri);
    }
}
