package io.github.vladimirshutkov.qaa.config;

import java.net.URI;

/** UI execution settings resolved from JVM properties, environment variables, and defaults. */
public record UiConfiguration(String baseUrl, BrowserName browserName, boolean headless) {
    private static final String BASE_URL_PROPERTY = "qaa.ui.base-url";
    private static final String BROWSER_PROPERTY = "qaa.ui.browser";
    private static final String HEADLESS_PROPERTY = "qaa.ui.headless";

    private static final String BASE_URL_ENVIRONMENT = "QAA_UI_BASE_URL";
    private static final String BROWSER_ENVIRONMENT = "QAA_UI_BROWSER";
    private static final String HEADLESS_ENVIRONMENT = "QAA_UI_HEADLESS";

    private static final String DEFAULT_BASE_URL = "https://www.saucedemo.com";
    private static final BrowserName DEFAULT_BROWSER = BrowserName.CHROMIUM;
    private static final boolean DEFAULT_HEADLESS = true;

    public UiConfiguration {
        validateBaseUrl(baseUrl);
    }

    public static UiConfiguration load() {
        return new UiConfiguration(
                setting(BASE_URL_PROPERTY, BASE_URL_ENVIRONMENT, DEFAULT_BASE_URL),
                BrowserName.from(setting(BROWSER_PROPERTY, BROWSER_ENVIRONMENT, DEFAULT_BROWSER.name())),
                parseBoolean(setting(HEADLESS_PROPERTY, HEADLESS_ENVIRONMENT, Boolean.toString(DEFAULT_HEADLESS)))
        );
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        return environmentValue != null && !environmentValue.isBlank() ? environmentValue : defaultValue;
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException("UI headless mode must be either true or false, but was: " + value);
    }

    private static void validateBaseUrl(String value) {
        URI uri = URI.create(value);
        if (!uri.isAbsolute() || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("UI base URL must be an absolute HTTP(S) URL, but was: " + value);
        }
    }
}
