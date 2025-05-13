package com.example.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.Builder;
import lombok.Data;

public class UserAgentParser {

    /**
     * Парсит строку User-Agent и возвращает информацию о браузере и ОС
     *
     * @param userAgentString строка User-Agent из запроса
     * @return объект с информацией о браузере и ОС
     */
    public static UserAgentInfo parseUserAgent(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return UserAgentInfo.builder()
                    .browser("Unknown")
                    .browserVersion("Unknown")
                    .operatingSystem("Unknown")
                    .deviceType("Unknown")
                    .build();
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();

        return UserAgentInfo.builder()
                .browser(browser.getName())
                .browserVersion(userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().getVersion() : "Unknown")
                .operatingSystem(os.getName())
                // .deviceType(os.getDeviceType().toString())
                .build();
    }

    // private static String getDeviceType(OperatingSystem os) {
    //     if (os.isMobileDevice()) {
    //         return "Mobile";
    //     } else {
    //         return "Desktop/Other";
    //     }
    // }

    @Data
    @Builder
    public static class UserAgentInfo {

        private String browser;
        private String browserVersion;
        private String operatingSystem;
        private String deviceType;
    }
}
