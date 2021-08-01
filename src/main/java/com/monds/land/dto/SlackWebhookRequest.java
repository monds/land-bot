package com.monds.land.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlackWebhookRequest {
    private final String text;
}
