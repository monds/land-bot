package com.monds.land.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class LandArticle implements Room {
    @Id
    private int id;
    private String name;
    private String floorInfo;
    private String dealOrWarrantPrc;
    private LocalDate articleConfirmYmd;
    private String articleFeatureDesc;
    private int monthlyMgmtCost;
    private String monthlyMgmtCostItemName;
    private String detailDescription;
    private String moveInPossibleYmd;
    private LocalDate buildingUseAprvYmd;
    private String tagList;
    private String exposureAddress;
    private boolean isNoloan = false;
    private String regionId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void setNoloan(boolean noloan) {
        isNoloan = noloan;
    }

    @Override
    public String getDomain() {
        return "네이버";
    }

    @Override
    public int getDeposit() {
        return Integer.parseInt(dealOrWarrantPrc);
    }

    @Override
    public String getAddress() {
        return exposureAddress;
    }

    @Override
    public URI getURI() {
        return URI.create("https://m.land.naver.com/article/info/" + getId());
    }

    @Override
    public String getDescription() {
        return articleFeatureDesc + "\n" + detailDescription;
    }

    @Override
    public LocalDate getApproveDate() {
        return buildingUseAprvYmd;
    }
}
