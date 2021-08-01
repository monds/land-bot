package com.monds.land.config;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monds.land.service.NaverAuthService;
import com.monds.land.service.NaverLandApi;
import com.monds.land.service.SlackMessageApi;
import com.monds.land.service.ZigbangApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
public class AppConfig {

    public static final String NAVER_HTTP_CLIENT = "naver-http-client";
    public static final String NAVER_LAND_API = "naver-land-api";
    public static final String ZIGBANG_HTTP_CLIENT = "zigbang-http-client";
    public static final String ZIGBANG_API = "zigbang-api";
    public static final String SLACK_HTTP_CLIENT = "slack-http-client";
    public static final String SLACK_API = "slack-api";

    @Bean(NAVER_HTTP_CLIENT)
    public OkHttpClient naverHttpClient(NaverAuthService authService) {
        return new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request();

                if (!StringUtils.hasText(request.header("Authorization"))) {
                    request = request.newBuilder()
                        .addHeader("Authorization", authService.getAuth(false))
                        .build();
                }

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;
                while ((response.code() == 302 || response.code() == 401) && tryCount < 3) {
                    log.info("Request is not successful - {}", tryCount);

                    tryCount++;
                    request.newBuilder()
                        .addHeader("Authorization", authService.getAuth(true));
                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            })
            .build();
    }

    @Bean(NAVER_LAND_API)
    public Retrofit naverRetrofit(@Qualifier(NAVER_HTTP_CLIENT) OkHttpClient client, Gson gson) {
        return new Retrofit.Builder().baseUrl("https://new.land.naver.com/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();
    }

    @Bean
    public NaverLandApi naverLandApi(@Qualifier(NAVER_LAND_API) Retrofit retrofit) {
        return retrofit.create(NaverLandApi.class);
    }

    @Bean(SLACK_HTTP_CLIENT)
    public OkHttpClient slackHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Bean(SLACK_API)
    public Retrofit slackRetrofit(@Qualifier(SLACK_HTTP_CLIENT) OkHttpClient client, Gson gson) {
        return new Retrofit.Builder().baseUrl("https://hooks.slack.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();
    }

    @Bean
    public SlackMessageApi slackWebhookApi(@Qualifier(SLACK_API) Retrofit retrofit) {
        return retrofit.create(SlackMessageApi.class);
    }

    @Bean(ZIGBANG_HTTP_CLIENT)
    public OkHttpClient zigbangHttpClient() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
//            .addInterceptor(interceptor)
            .build();
    }

    @Bean(ZIGBANG_API)
    public Retrofit zigbangRetrofit(@Qualifier(ZIGBANG_HTTP_CLIENT) OkHttpClient client) {
        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDate.class, GsonConfig.localDateDeserializer())
            .registerTypeAdapter(LocalDateTime.class, GsonConfig.localDateTimeDeserializer())
            .create();

        return new Retrofit.Builder().baseUrl("https://apis.zigbang.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();
    }

    @Bean
    public ZigbangApi zigbangApi(@Qualifier(ZIGBANG_API) Retrofit retrofit) {
        return retrofit.create(ZigbangApi.class);
    }

    @Bean
    public Mustache articleNotifyTemplate() {
        MustacheFactory mf = new DefaultMustacheFactory();
        return mf.compile("article.notify.mustache");
    }
}
