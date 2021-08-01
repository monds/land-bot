package com.monds.land.service;

import com.monds.land.dto.ZigbangItemIdsResponse;
import com.monds.land.dto.ZigbangItemResponse;
import com.monds.land.dto.ZigbangItemsListRequest;
import com.monds.land.dto.ZigbangItemsListResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface ZigbangApi {
    @GET("v3/items/ad/{subwayId}")
    Call<ZigbangItemIdsResponse> getItemIdsBySubwayId(@Path("subwayId") int subwayId, @QueryMap Map<String, String> filters);

    @POST("v2/items/list")
    Call<ZigbangItemsListResponse> getItemsList(@Body ZigbangItemsListRequest request);

    @GET("v2/items/{id}")
    Call<ZigbangItemResponse> getItemById(@Path("id") int id);
}
