package HelperClasses;

public class HistoryItem {
    private String title;
    private String details;

    public HistoryItem(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }
}