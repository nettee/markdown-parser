package me.nettee.markdown.dom;

import java.util.List;

/**
 * 段落
 */
public abstract class Paragraph extends MarkdownElement {

    public abstract List<String> lineStrings();

    @Override
    public String toString() {
        return String.join("\n", lineStrings());
    }
}
