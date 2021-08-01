package com.monds.land.common;

import java.text.DecimalFormat;

public class NumberUtil {
    public static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static String toHumanReadable(int money) {
        String sb = (money / 10000 > 0 ? money / 10000 + "억 " : "") +
            (money % 10000 > 0 ? formatter.format(money % 10000) : "");
        return sb.trim();
    }
}
