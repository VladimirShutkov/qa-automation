package io.github.vladimirshutkov.qaa.api;

import io.github.vladimirshutkov.qaa.config.ApiConfiguration;
import io.github.vladimirshutkov.qaa.models.Booking;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

/** REST client for Restful Booker booking endpoints. */
public final class BookingClient {
    private static final Logger API_LOGGER = LoggerFactory.getLogger("API");
    private static final int CONNECT_TIMEOUT_MILLIS = 10_000;
    private static final int SOCKET_TIMEOUT_MILLIS = 30_000;
    private final ApiConfiguration configuration;
    private String authenticationToken;

    public BookingClient(ApiConfiguration configuration) {
        this.configuration = configuration;
    }

    public Response getBookingIds() {
        API_LOGGER.info("GET {}/booking", configuration.baseUrl());
        return request()
                .when()
                .get("/booking");
    }

    public Response getBooking(int bookingId) {
        API_LOGGER.info("GET /booking/{}", bookingId);
        return request()
                .when()
                .get("/booking/{bookingId}", bookingId);
    }

    public Response createBooking(Booking booking) {
        API_LOGGER.info("POST {}/booking", configuration.baseUrl());
        return request()
                .body(booking)
                .when()
                .post("/booking");
    }

    public void authenticate() {
        API_LOGGER.info("POST /auth");
        Response response = request()
                .body(Map.of(
                        "username", configuration.username(),
                        "password", configuration.password()
                ))
                .when()
                .post("/auth");
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Authentication failed with HTTP " + response.statusCode());
        }

        authenticationToken = response.path("token");
        if (authenticationToken == null || authenticationToken.isBlank()) {
            throw new IllegalStateException("Authentication response does not contain a token");
        }
        API_LOGGER.info("Authentication token received");
    }

    public Response updateBooking(int bookingId, Booking booking) {
        API_LOGGER.info("PUT /booking/{}", bookingId);
        Response response = request()
                .cookie("token", authenticationToken)
                .body(booking)
                .when()
                .put("/booking/{bookingId}", bookingId);
        API_LOGGER.info("Response status: {}", response.statusCode());
        return response;
    }

    public Response updateBookingWithoutAuthentication(int bookingId, Booking booking) {
        API_LOGGER.info("PUT /booking/{} without authentication", bookingId);
        Response response = request()
                .body(booking)
                .when()
                .put("/booking/{bookingId}", bookingId);
        API_LOGGER.info("Response status: {}", response.statusCode());
        return response;
    }

    public Response deleteBooking(int bookingId) {
        API_LOGGER.info("DELETE /booking/{}", bookingId);
        Response response = request()
                .cookie("token", authenticationToken)
                .when()
                .delete("/booking/{bookingId}", bookingId);
        API_LOGGER.info("Response status: {}", response.statusCode());
        return response;
    }

    private RequestSpecification request() {
        return given()
                .baseUri(configuration.baseUrl())
                .contentType("application/json")
                .config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECT_TIMEOUT_MILLIS)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT_MILLIS)))
                .filter(new AllureRestAssuredFilter());
    }

}
