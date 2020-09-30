package me.nettee.markdown.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 公式块
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class MathBlock extends Paragraph {

    public static final String PREFIX = "$$";

    /**
     * 公式内容
     */
    private final List<String> lines;

    public MathBlock(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public List<String> lineStrings() {
        List<String> res = new ArrayList<>();
        res.add(PREFIX);
        res.addAll(lines);
        res.add(PREFIX);
        return res;
    }
}
