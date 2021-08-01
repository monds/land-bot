package com.monds.land.dto;

import com.google.common.base.Joiner;
import com.monds.land.domain.LandArticle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
public class ArticleDetailResponse {
    private ArticleDetail articleDetail;
    private ArticleFacility articleFacility;
    private List<ArticlePhoto> articlePhotos;
    private ArticlePrice articlePrice;

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleDetail {
        private int monthlyManagementCost;
        private String monthlyManagementCostIncludeItemName;
        private String detailDescription;
        private String moveInPossibleYmd;
        private List<String> tagList;
        private String exposureAddress;
    }

    @Getter
    public static class ArticleFacility {
        private LocalDate buildingUseAprvYmd;
    }

    @Getter
    public static class ArticlePhoto {}

    @Getter
    public static class ArticlePrice {
        private int warrantPrice;
    }

    public LandArticle toLandArticle(String regionId, ArticlesResponse.Article article) {
        return LandArticle.builder()
            .id(Integer.parseInt(article.getArticleNo()))
            .name(article.getArticleName())
            .floorInfo(article.getFloorInfo())
            .dealOrWarrantPrc(String.valueOf(articlePrice.warrantPrice))
            .articleConfirmYmd(article.getArticleConfirmYmd())
            .articleFeatureDesc(article.getArticleFeatureDesc())
            .monthlyMgmtCost(articleDetail.getMonthlyManagementCost())
            .monthlyMgmtCostItemName(articleDetail.getMonthlyManagementCostIncludeItemName())
            .detailDescription(articleDetail.getDetailDescription())
            .moveInPossibleYmd(articleDetail.getMoveInPossibleYmd())
            .buildingUseAprvYmd(articleFacility.getBuildingUseAprvYmd())
            .tagList(Joiner.on(',').join(articleDetail.getTagList()))
            .exposureAddress(articleDetail.getExposureAddress())
            .regionId(regionId)
            .build();

    }

    public boolean hasPhotos() {
        return articlePhotos != null && !articlePhotos.isEmpty();
    }
}
