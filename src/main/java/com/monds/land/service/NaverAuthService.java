package com.monds.land.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.monds.land.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@RequiredArgsConstructor
@Service
public class NaverAuthService {
    private final AppProperties properties;
    private String auth;

    public String getAuth(boolean recreate) {
        if (!recreate && StringUtils.hasText(auth)) {
            return auth;
        }
        auth = getAuthorization();
        return auth;
    }

    private String getAuthorization() {
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingDriverExecutable(new File(properties.getChromeDriverPath()))
            .usingAnyFreePort()
            .build();

        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggingPreferences preferences = new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions options = new ChromeOptions();
        options.setCapability("goog:loggingPrefs", preferences);
        options.setCapability(CapabilityType.LOGGING_PREFS, preferences);
        options.addArguments("headless");

        WebDriver driver = new ChromeDriver(service, options);
        driver.get("https://new.land.naver.com/rooms");
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        Gson gson = new Gson();
        LogEntries logs = driver.manage().logs().get(LogType.PERFORMANCE);
        String authorization = "";
        for (LogEntry entry : logs) {
            String message = entry.getMessage();
            if (message.contains("authorization")) {
                JsonObject messageObj = gson.fromJson(message, JsonObject.class)
                    .getAsJsonObject("message");

                if (messageObj.get("method").getAsString().equals("Network.requestWillBeSent")) {
                    authorization = messageObj.getAsJsonObject("params")
                        .getAsJsonObject("request")
                        .getAsJsonObject("headers")
                        .get("authorization")
                        .getAsString();
                    break;
                }
            }
        }

        driver.quit();
        service.stop();

        return authorization;
    }
}
