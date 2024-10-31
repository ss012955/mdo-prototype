package com.example.prototype;

import java.util.List;

public class DashboardContent {
    private String type;
    private List<String> images;       // For announcements slideshow
    private List<String> appointments; // For upcoming appointments
    private String trivia;             // For trivia text

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
}