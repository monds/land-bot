package com.monds.land.service;

import com.monds.land.dto.SlackWebhookRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SlackMessageApi {
    @POST("services/T026G8L0NUE/B026G963JEA/xKVmUlmf3YUgLMLPNJGM7fKC")
    Call<String> sendMessage(@Body SlackWebhookRequest request);
}
