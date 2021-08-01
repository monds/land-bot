package com.monds.land.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ZigbangItemIdsResponse {
    private List<ListItem> listItems;
    private int totalCount;

    @ToString
    @Getter
    public static class ListItem {
        private SimpleItem simpleItem;

        @ToString
        @Getter
        public static class SimpleItem {
            private int itemId;
        }
    }
}
