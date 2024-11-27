package HelperClasses;
public class TriviaItem {
    private String title;
    private String text;

    // Constructor
    public TriviaItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}