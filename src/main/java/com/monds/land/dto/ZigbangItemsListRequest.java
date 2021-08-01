package com.monds.land.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ZigbangItemsListRequest {
    private final String domain = "zigbang";
    @SerializedName("withCoalition")
    private final boolean withCoalition = true;
    private final List<Integer> itemIds;

    public ZigbangItemsListRequest(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
