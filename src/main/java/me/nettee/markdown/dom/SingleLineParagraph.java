package me.nettee.markdown.dom;

import java.util.Collections;
import java.util.List;

/**
 * 只有一行的段落
 */
public class SingleLineParagraph extends Paragraph {

    @Override
    public List<String> lineStrings() {
        return Collections.singletonList(toString());
    }
}
