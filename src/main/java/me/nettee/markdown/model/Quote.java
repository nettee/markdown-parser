package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * 引用
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class Quote extends Paragraph {

    public static final String PREFIX = ">";

    /**
     * 引用中的段落
     */
    private List<String> paragraphs;

    @Override
    public List<String> lineStrings() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
