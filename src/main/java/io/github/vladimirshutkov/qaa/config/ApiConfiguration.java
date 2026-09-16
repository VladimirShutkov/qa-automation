package io.github.vladimirshutkov.qaa.config;

import java.net.URI;

/** API execution settings resolved from JVM properties, environment variables, and defaults. */
public record ApiConfiguration(String baseUrl, String username, String password) {
    private static final String BASE_URL_PROPERTY = "qaa.api.base-url";
    private static final String BASE_URL_ENVIRONMENT = "QAA_API_BASE_URL";
    private static final String USERNAME_PROPERTY = "qaa.api.username";
    private static final String USERNAME_ENVIRONMENT = "QAA_API_USERNAME";
    private static final String PASSWORD_PROPERTY = "qaa.api.password";
    private static final String PASSWORD_ENVIRONMENT = "QAA_API_PASSWORD";
    private static final String DEFAULT_BASE_URL = "https://restful-booker.herokuapp.com";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "password123";

    public ApiConfiguration {
        validateBaseUrl(baseUrl);
    }

    public static ApiConfiguration load() {
        return new ApiConfiguration(
                resolveValue(BASE_URL_PROPERTY, BASE_URL_ENVIRONMENT, DEFAULT_BASE_URL),
                resolveValue(USERNAME_PROPERTY, USERNAME_ENVIRONMENT, DEFAULT_USERNAME),
                resolveValue(PASSWORD_PROPERTY, PASSWORD_ENVIRONMENT, DEFAULT_PASSWORD)
        );
    }

    private static String resolveValue(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        return environmentValue != null && !environmentValue.isBlank() ? environmentValue : defaultValue;
    }

    private static void validateBaseUrl(String value) {
        URI uri = URI.create(value);
        if (!uri.isAbsolute() || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("API base URL must be an absolute HTTP(S) URL, but was: " + value);
        }
    }
}
