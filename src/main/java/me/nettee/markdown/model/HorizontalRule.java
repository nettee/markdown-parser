package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 水平分隔线
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HorizontalRule extends SingleLineParagraph {

    public static final String PREFIX = "---";

    @Override
    public String toString() {
        return PREFIX;
    }
}
