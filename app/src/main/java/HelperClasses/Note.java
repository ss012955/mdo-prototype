package HelperClasses;

public class Note {
    private String title;
    private String dateTime;
    private String symptoms;
    private String mood;
    private String medicine;
    private String noteId; // Add a field for the ID

    // Constructor
    public Note(String noteId, String title, String dateTime, String symptoms, String mood, String medicine) {
        this.noteId = noteId; // Initialize the ID
        this.title = title;
        this.dateTime = dateTime;
        this.symptoms = symptoms;
        this.mood = mood;
        this.medicine = medicine;

    }

    public String getId() {
        return noteId; // Return the note ID
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getMood() {
        return mood;
    }

    public String getMedicine() {
        return medicine;
    }

    // Setters (optional, if you need them)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }
}