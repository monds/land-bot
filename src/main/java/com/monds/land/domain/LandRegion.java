package com.monds.land.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table
public class LandRegion {
    @Id
    private String id;
    private String name;
    private String nameGu;

    public String getAddressFullName() {
        return nameGu + " " + name;
    }
}
