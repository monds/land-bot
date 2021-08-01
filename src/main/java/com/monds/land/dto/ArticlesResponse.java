package com.monds.land.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticlesResponse {
    private List<Article> articleList;

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        private String articleNo;
        private String articleName;
        private String floorInfo;
        private String dealOrWarrantPrc;
        private LocalDate articleConfirmYmd;
        private String articleFeatureDesc;

        public boolean invalid(LocalDate baseDate) {
            return articleConfirmYmd.compareTo(baseDate) < 0;
        }

        public boolean isBasementFloor() {
            return floorInfo.startsWith("B");
        }
    }
}
