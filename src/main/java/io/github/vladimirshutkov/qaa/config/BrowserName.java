package io.github.vladimirshutkov.qaa.config;

import java.util.Locale;

/** Supported Playwright browser engines. */
public enum BrowserName {
    CHROMIUM,
    FIREFOX,
    WEBKIT;

    public static BrowserName from(String value) {
        try {
            return BrowserName.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported browser '" + value + "'. Supported values: chromium, firefox, webkit.",
                    exception
            );
        }
    }
}
