package HelperClasses;

public class Note {
    private String title;
    private String details;

    public Note(String title, String details) {
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
