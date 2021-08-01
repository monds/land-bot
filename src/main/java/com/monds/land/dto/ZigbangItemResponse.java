package com.monds.land.dto;

import com.google.gson.annotations.SerializedName;
import com.monds.land.config.GsonConfig;
import com.monds.land.domain.Room;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
public class ZigbangItemResponse implements Room {
    private Item item;

    @Override
    public String getDomain() {
        return "직방";
    }

    @Override
    public int getId() {
        return item.getItemId();
    }

    @Override
    public int getDeposit() {
        return item.deposit;
    }

    @Override
    public String getAddress() {
        return item.local1 + " " + (StringUtils.hasText(item.jibunAddress) ? item.jibunAddress : item.address);
    }

    @Override
    public URI getURI() {
        return URI.create("https://www.zigbang.com/home/oneroom/items/" + getId());
    }

    @Override
    public String getDescription() {
        return item.title + "\n" + item.description;
    }

    @Override
    public LocalDate getApproveDate() {
        try {
            return LocalDate.parse(StringUtils.trimAllWhitespace(item.approveDate), GsonConfig.DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    @ToString
    @Getter
    public static class Item {
        private int itemId;

        @SerializedName("보증금액")
        private int deposit;
        private String address;
        @SerializedName("jibunAddress")
        private String jibunAddress;
        private String local1;

        private String title;
        private String description;

        private String approveDate;

        @SerializedName("상태확인At")
        private LocalDateTime createdAt;

        private String floorString;
    }
}
