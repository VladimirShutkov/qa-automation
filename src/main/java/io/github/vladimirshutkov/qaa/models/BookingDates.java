package io.github.vladimirshutkov.qaa.models;

/** Booking check-in and check-out dates in ISO-8601 date format. */
public final class BookingDates {
    private final String checkin;
    private final String checkout;

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public String getCheckout() {
        return checkout;
    }
}
