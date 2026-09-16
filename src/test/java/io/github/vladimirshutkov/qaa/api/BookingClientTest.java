package io.github.vladimirshutkov.qaa.api;

import io.github.vladimirshutkov.qaa.config.ApiConfiguration;
import io.github.vladimirshutkov.qaa.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

class BookingClientTest {
    private static final Logger TEST_LOGGER = LoggerFactory.getLogger("TEST");
    private static final Logger ASSERT_LOGGER = LoggerFactory.getLogger("ASSERT");
    private final List<Integer> bookingIdsForCleanup = new ArrayList<>();

    @AfterEach
    void deleteCreatedBookings() {
        if (bookingIdsForCleanup.isEmpty()) {
            return;
        }

        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());
        bookingClient.authenticate();
        for (int bookingId : bookingIdsForCleanup) {
            Response response = bookingClient.deleteBooking(bookingId);
            assertEquals(201, response.statusCode(), "Cleanup should delete booking " + bookingId + ".");
        }
    }

    @Test
    @DisplayName("Get booking IDs successfully")
    void shouldReturnBookingIds() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldReturnBookingIds");
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response response = bookingClient.getBookingIds();

        ASSERT_LOGGER.info("Verifying GET /booking returns HTTP 200");
        assertEquals(200, response.statusCode(), "GET /booking should return HTTP 200.");
        ASSERT_LOGGER.info("Verifying GET /booking response is not empty");
        assertFalse(response.jsonPath().getList("$").isEmpty(), "GET /booking response should not be empty.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldReturnBookingIds");
    }

    @Test
    @DisplayName("Create booking successfully")
    void shouldCreateBooking() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldCreateBooking");
        Booking booking = BookingTestData.createBooking();
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response response = bookingClient.createBooking(booking);

        ASSERT_LOGGER.info("Verifying POST /booking returns HTTP 200");
        assertEquals(200, response.statusCode(), "POST /booking should return HTTP 200.");
        ASSERT_LOGGER.info("Verifying created booking ID is present");
        assertTrue(response.jsonPath().getInt("bookingid") > 0, "Created booking should have a booking ID.");
        bookingIdsForCleanup.add(response.jsonPath().getInt("bookingid"));
        ASSERT_LOGGER.info("Verifying created booking fields");
        assertEquals(booking.getFirstname(), response.jsonPath().getString("booking.firstname"), "Created booking firstname should match the request.");
        assertEquals(booking.getLastname(), response.jsonPath().getString("booking.lastname"), "Created booking lastname should match the request.");
        assertEquals(booking.getTotalprice(), response.jsonPath().getInt("booking.totalprice"), "Created booking total price should match the request.");
        assertEquals(booking.isDepositpaid(), response.jsonPath().getBoolean("booking.depositpaid"), "Created booking deposit-paid flag should match the request.");
        assertEquals(booking.getBookingdates().getCheckin(), response.jsonPath().getString("booking.bookingdates.checkin"), "Created booking check-in date should match the request.");
        assertEquals(booking.getBookingdates().getCheckout(), response.jsonPath().getString("booking.bookingdates.checkout"), "Created booking check-out date should match the request.");
        assertEquals(booking.getAdditionalneeds(), response.jsonPath().getString("booking.additionalneeds"), "Created booking additional needs should match the request.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldCreateBooking");
    }

    @Test
    @DisplayName("Get non-existing booking returns 404")
    void shouldReturnNotFoundForNonExistentBooking() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldReturnNotFoundForNonExistentBooking");
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response response = bookingClient.getBooking(999_999_999);

        ASSERT_LOGGER.info("Verifying GET /booking/{id} for a non-existent booking returns HTTP 404");
        assertEquals(404, response.statusCode(), "GET /booking/{id} for a non-existent booking should return HTTP 404.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldReturnNotFoundForNonExistentBooking");
    }

    @Test
    @DisplayName("Update booking without authentication is rejected")
    void shouldRejectUpdateWithoutAuthentication() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldRejectUpdateWithoutAuthentication");
        Booking booking = BookingTestData.bookingForUpdate();
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response createResponse = bookingClient.createBooking(booking);
        ASSERT_LOGGER.info("Verifying booking creation for unauthenticated update returns HTTP 200");
        assertEquals(200, createResponse.statusCode(), "Booking creation for unauthenticated update should return HTTP 200.");
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        ASSERT_LOGGER.info("Verifying booking ID for unauthenticated update is present");
        assertTrue(bookingId > 0, "Booking to update should have a booking ID.");
        bookingIdsForCleanup.add(bookingId);

        Response updateResponse = bookingClient.updateBookingWithoutAuthentication(bookingId, booking);

        ASSERT_LOGGER.info("Verifying PUT /booking/{id} without authentication returns HTTP 403");
        assertEquals(403, updateResponse.statusCode(), "PUT /booking/{id} without authentication should return HTTP 403.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldRejectUpdateWithoutAuthentication");
    }

    @Test
    @DisplayName("Update booking with authentication")
    void shouldUpdateBooking() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldUpdateBooking");
        Booking initialBooking = BookingTestData.bookingForUpdate();
        Booking updatedBooking = BookingTestData.updatedBooking();
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response createResponse = bookingClient.createBooking(initialBooking);
        ASSERT_LOGGER.info("Verifying booking creation for update returns HTTP 200");
        assertEquals(200, createResponse.statusCode(), "Booking creation for update should return HTTP 200.");
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        ASSERT_LOGGER.info("Verifying booking ID for update is present");
        assertTrue(bookingId > 0, "Booking to update should have a booking ID.");
        bookingIdsForCleanup.add(bookingId);

        bookingClient.authenticate();
        Response updateResponse = bookingClient.updateBooking(bookingId, updatedBooking);

        ASSERT_LOGGER.info("Verifying PUT /booking/{id} returns HTTP 200");
        assertEquals(200, updateResponse.statusCode(), "PUT /booking/{id} should return HTTP 200.");
        ASSERT_LOGGER.info("Verifying updated booking fields");
        assertEquals(updatedBooking.getFirstname(), updateResponse.jsonPath().getString("firstname"), "Updated booking firstname should match the request.");
        assertEquals(updatedBooking.getLastname(), updateResponse.jsonPath().getString("lastname"), "Updated booking lastname should match the request.");
        assertEquals(updatedBooking.getTotalprice(), updateResponse.jsonPath().getInt("totalprice"), "Updated booking total price should match the request.");
        assertEquals(updatedBooking.isDepositpaid(), updateResponse.jsonPath().getBoolean("depositpaid"), "Updated booking deposit-paid flag should match the request.");
        assertEquals(updatedBooking.getBookingdates().getCheckin(), updateResponse.jsonPath().getString("bookingdates.checkin"), "Updated booking check-in date should match the request.");
        assertEquals(updatedBooking.getBookingdates().getCheckout(), updateResponse.jsonPath().getString("bookingdates.checkout"), "Updated booking check-out date should match the request.");
        assertEquals(updatedBooking.getAdditionalneeds(), updateResponse.jsonPath().getString("additionalneeds"), "Updated booking additional needs should match the request.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldUpdateBooking");
    }

    @Test
    @DisplayName("Delete booking successfully")
    void shouldDeleteBooking() {
        TEST_LOGGER.info("Starting: BookingClientTest.shouldDeleteBooking");
        Booking booking = BookingTestData.createBooking();
        BookingClient bookingClient = new BookingClient(ApiConfiguration.load());

        Response createResponse = bookingClient.createBooking(booking);
        ASSERT_LOGGER.info("Verifying booking creation for deletion returns HTTP 200");
        assertEquals(200, createResponse.statusCode(), "Booking creation for deletion should return HTTP 200.");
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        ASSERT_LOGGER.info("Verifying booking ID for deletion is present");
        assertTrue(bookingId > 0, "Booking to delete should have a booking ID.");

        bookingClient.authenticate();
        Response deleteResponse = bookingClient.deleteBooking(bookingId);

        ASSERT_LOGGER.info("Verifying DELETE /booking/{id} returns HTTP 201");
        assertEquals(201, deleteResponse.statusCode(), "DELETE /booking/{id} should return HTTP 201.");

        Response getResponse = bookingClient.getBooking(bookingId);
        ASSERT_LOGGER.info("Verifying deleted booking is no longer available");
        assertEquals(404, getResponse.statusCode(), "Deleted booking should return HTTP 404.");
        TEST_LOGGER.info("PASSED: BookingClientTest.shouldDeleteBooking");
    }
}
