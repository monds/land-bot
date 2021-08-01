package com.monds.land.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GsonConfig {
    public static final LocalDateDeserializer LOCAL_DATE_DESERIALIZER = new LocalDateDeserializer();
    public static final LocalDateTimeDeserializer LOCAL_DATE_TIME_DESERIALIZER = new LocalDateTimeDeserializer();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
        "[yyyyMMdd][yyyy-MM-dd][yyyy.M.d.][yyyy.M.d][yy.MM]"
    );

    public static LocalDateDeserializer localDateDeserializer() {
        return LOCAL_DATE_DESERIALIZER;
    }

    public static LocalDateTimeDeserializer localDateTimeDeserializer() {
        return LOCAL_DATE_TIME_DESERIALIZER;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, localDateDeserializer())
            .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer())
            .create();
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDate.parse(json.getAsString(), DATE_FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
