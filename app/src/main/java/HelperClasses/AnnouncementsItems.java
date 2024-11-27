package HelperClasses;

public class AnnouncementsItems {
    private final String title;
    private final String text;
    private final String imageUrl;

    public AnnouncementsItems(String title, String text, String imageUrl) {
        this.title = title;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}