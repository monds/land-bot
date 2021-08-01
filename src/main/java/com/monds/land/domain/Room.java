package com.monds.land.domain;

import com.monds.land.common.DescriptionUtils;
import com.monds.land.common.NumberUtil;

import java.net.URI;
import java.time.LocalDate;

public interface Room {

    default boolean hasMortgage() {
        return !DescriptionUtils.noMortgage(getDescription());
    }

    default String formattedDeposit() {
        return NumberUtil.toHumanReadable(getDeposit());
    }

    String getDomain();

    int getId();

    int getDeposit();

    String getAddress();

    URI getURI();

    String getDescription();

    LocalDate getApproveDate();
}
