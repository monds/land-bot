package com.monds.land.common;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class DescriptionUtils {

    public static final String[] STOP_WORDS = {
        "대출불가",
        "전세대출불",
        "전세대출불가",
        "전세대출 불가",
        "대출 안됩니다",
        "전세대출은 안됩니다",
        "대출안됨",
        "전세자금대출 불가",
        "대출X"
    };
    public static final String[] NO_MORTGAGE_WORDS = {"융무", "융X", "융자없", "융자무", "융자X", "근저당없", "근저당X"};
    public static final String[] FIRST_MOVE_IN_WORDS = {"첫입주", "준공예정"};

    public static boolean unableLoan(String description) {
        return StringUtils.hasText(description) && Arrays.stream(STOP_WORDS).anyMatch(description::contains);
    }

    public static boolean firstMoveIn(String description) {
        return StringUtils.hasText(description) && Arrays.stream(FIRST_MOVE_IN_WORDS).anyMatch(description::contains);
    }

    public static boolean noMortgage(String description) {
        return StringUtils.hasText(description) && Arrays.stream(NO_MORTGAGE_WORDS).anyMatch(description::contains);
    }
}
