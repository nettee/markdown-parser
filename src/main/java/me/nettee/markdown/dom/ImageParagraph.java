package me.nettee.markdown.dom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 图片段落
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class ImageParagraph extends SingleLineParagraph {

    /**
     * 图片
     */
    private final Image image;

    public ImageParagraph(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return image.toString();
    }
}
