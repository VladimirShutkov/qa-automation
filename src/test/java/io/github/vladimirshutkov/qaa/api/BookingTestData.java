package io.github.vladimirshutkov.qaa.api;

import io.github.vladimirshutkov.qaa.models.Booking;
import io.github.vladimirshutkov.qaa.models.BookingDates;

/** Factory for Restful Booker test payloads. */
public final class BookingTestData {
    private BookingTestData() {
    }

    public static Booking createBooking() {
        return new Booking(
                "API",
                "Automation",
                150,
                true,
                new BookingDates("2026-10-01", "2026-10-05"),
                "Breakfast"
        );
    }

    public static Booking bookingForUpdate() {
        return new Booking(
                "Update",
                "Original",
                200,
                false,
                new BookingDates("2026-11-01", "2026-11-04"),
                "None"
        );
    }

    public static Booking updatedBooking() {
        return new Booking(
                "Updated",
                "Booking",
                275,
                true,
                new BookingDates("2026-11-10", "2026-11-15"),
                "Late checkout"
        );
    }
}
