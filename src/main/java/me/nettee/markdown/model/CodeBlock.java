package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码块
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class CodeBlock extends Paragraph {

    public static final String PREFIX = "```";

    /**
     * 代码语言
     */
    private final String language;

    /**
     * 代码内容
     */
    private final List<String> lines;

    public CodeBlock(String language, List<String> lines) {
        this.language = language;
        this.lines = lines;
    }

    @Override
    public List<String> lineStrings() {
        List<String> res = new ArrayList<>();
        res.add(PREFIX + language);
        res.addAll(lines);
        res.add(PREFIX);
        return res;
    }
}
