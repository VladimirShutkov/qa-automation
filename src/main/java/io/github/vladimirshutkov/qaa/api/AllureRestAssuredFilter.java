package io.github.vladimirshutkov.qaa.api;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.net.URI;
import java.util.regex.Pattern;

/** Adds sanitized HTTP request and response diagnostics to the current Allure test. */
public final class AllureRestAssuredFilter implements Filter {
    private static final String REDACTED = "[REDACTED]";
    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
            "(?i)(\"(?:username|password|passwd|pwd|token|authorization|cookie|session(?:id)?|secret|api[_-]?key|access[_-]?token|refresh[_-]?token|client[_-]?secret)\"\\s*:\\s*)\"(?:\\\\.|[^\"\\\\])*\""
    );

    @Override
    public Response filter(
            FilterableRequestSpecification request,
            FilterableResponseSpecification responseSpecification,
            FilterContext context
    ) {
        attach("HTTP Request", requestDetails(request));
        Response response = context.next(request, responseSpecification);
        attach("HTTP Response", responseDetails(response));
        return response;
    }

    private static String requestDetails(FilterableRequestSpecification request) {
        String body = request.getBody() == null ? "" : sanitize(request.getBody().toString());
        return "Method: " + request.getMethod() + System.lineSeparator()
                + "Endpoint: " + URI.create(request.getURI()).getRawPath() + System.lineSeparator()
                + "Body:" + System.lineSeparator() + body;
    }

    private static String responseDetails(Response response) {
        return "Status: " + response.statusCode() + System.lineSeparator()
                + "Body:" + System.lineSeparator() + sanitize(response.asString());
    }

    private static void attach(String name, String content) {
        Allure.addAttachment(name, "text/plain", content, ".txt");
    }

    private static String sanitize(String content) {
        return SENSITIVE_JSON_FIELD.matcher(content).replaceAll("$1\"" + REDACTED + "\"");
    }
}
