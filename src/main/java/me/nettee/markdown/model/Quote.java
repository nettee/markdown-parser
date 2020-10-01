package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private final List<Paragraph> paragraphs;

    public Quote(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    @Override
    public List<String> lineStrings() {
        List<String> lines = paragraphs.stream()
                .map(Paragraph::lineStrings)
                .reduce((res, paragraphLines) -> {
                    res.add("");
                    res.addAll(paragraphLines);
                    return res;
                })
                .orElse(Collections.emptyList());
        return lines.stream().map(line -> "> " + line).collect(Collectors.toList());
    }
}
