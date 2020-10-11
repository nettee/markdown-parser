package me.nettee.markdown.dom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 列表项
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class ListItem extends InlineElement {

    /**
     * 文本 TODO 支持更复杂的内容
     */
    private final String text;

    public ListItem(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
