package me.nettee.markdown.model;

import com.google.common.base.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 标题
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class Heading extends SingleLineParagraph {

    public static final String PREFIX = "#";

    /**
     * 标题级别，取值范围 1~6
     */
    private final int level;

    /**
     * 标题文本
     */
    private final String text;

    public Heading(int level, String text) {
        this.level = level;
        this.text = text;
    }

    @Override
    public String toString() {
        return Strings.repeat(PREFIX, level) + " " + text;
    }
}
