package HelperClasses;

public class HistoryItem {
    private int bookingId;
    private String title;
    private String details;

    public HistoryItem(int bookingId, String title, String details) {
        this.bookingId = bookingId;
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public int getBookingId() {
        return bookingId;
    }

}