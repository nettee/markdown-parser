package me.nettee.markdown.dom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 无序列表
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class UnorderedList extends Paragraph {

    /**
     * 列表项
     */
    private final List<ListItem> listItemList;

    public UnorderedList(List<ListItem> listItemList) {
        this.listItemList = listItemList;
    }

    @Override
    public List<String> lineStrings() {
        return listItemList.stream()
                .map(Object::toString)
                .map(text -> "* " + text)
                .collect(Collectors.toList());
    }
}
