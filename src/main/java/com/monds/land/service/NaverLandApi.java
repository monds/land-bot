package com.monds.land.service;

import com.monds.land.dto.ArticleDetailResponse;
import com.monds.land.dto.ArticlesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface NaverLandApi {

    @GET("articles")
    Call<ArticlesResponse> getArticles(@QueryMap Map<String, String> filters);

    @GET("articles/{articleNo}")
    Call<ArticleDetailResponse> getArticleDetailById(@Path("articleNo") int articleNo);
}
