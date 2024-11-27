package com.example.prototype;

import java.util.List;

public class DashboardContent {
    private String type;
    private List<String> images;       // For announcements slideshow
    private List<String> appointments; // For upcoming appointments
    private String trivia;             // For trivia text
    private String triviaTitle;
    private String announcementTitle;
    private String announcementDescrip;
    private String imageUrl;// Add a field for trivia title
    // General Constructor
    public DashboardContent(String type, List<String> images, List<String> appointments, String trivia) {
        this.type = type;

        switch (type) {
            case "Announcements":
                this.images = images;
                break;
            case "Appointments":
                this.appointments = appointments;
                break;
            case "Trivia":
                this.trivia = trivia;
                break;
            default:
                throw new IllegalArgumentException("Invalid type specified");
        }
    }

    // Getters
    public String getType() {
        return type;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getAppointments() {
        return appointments;
    }

    public String getTrivia() {
        return trivia;
    }

    // Getter and setter for trivia title
    public String getTriviaTitle() {
        return triviaTitle;
    }

    public void setTriviaTitle(String triviaTitle) {
        this.triviaTitle = triviaTitle;
    }

    // Getter and setter for announcementTitle
    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    // Getter and setter for announcementDescrip
    public String getAnnouncementDescrip() {
        return announcementDescrip;
    }

    public void setAnnouncementDescrip(String announcementDescrip) {
        this.announcementDescrip = announcementDescrip;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public String getImageUrl(){
        return imageUrl;
    }

}