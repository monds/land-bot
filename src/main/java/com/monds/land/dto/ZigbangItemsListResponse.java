package com.monds.land.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ZigbangItemsListResponse {
    private List<Item> items;

    @ToString
    @Getter
    public static class Item {
        private int itemId;
        private LocalDateTime regDate;
    }
}
