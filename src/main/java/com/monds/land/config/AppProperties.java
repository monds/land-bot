package com.monds.land.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
public class AppProperties {
    private final String chromeDriverPath;

    public AppProperties(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }
}
