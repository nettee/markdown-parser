package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class NormalParagraph extends Paragraph {

    /**
     * 段落行
     */
    private final List<String> lines;

    public NormalParagraph(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public List<String> lineStrings() {
        return lines;
    }
}
