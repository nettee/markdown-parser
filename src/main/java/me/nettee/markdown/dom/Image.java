package me.nettee.markdown.dom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 图片元素
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class Image extends InlineElement {

    /**
     * 图片说明
     */
    private final String caption;

    /**
     * 图片 URI
     */
    private final String uri;

    public Image(String caption, String uri) {
        this.caption = caption;
        this.uri = uri;
    }

    public boolean isLocal() {
        return !isOnline();
    }

    public boolean isOnline() {
        return StringUtils.startsWithAny(uri, "http://", "https://");
    }

    @Override
    public String toString() {
        return String.format("![%s](%s)", caption, uri);
    }
}
