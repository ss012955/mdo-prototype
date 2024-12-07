package HelperClasses;

public class AppointmentDaysClass {
    private String dateTime;
    private String status;
    private String createdAt;
    public AppointmentDaysClass(String dateTime, String status, String createdAt) {
        this.dateTime = dateTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getYear() {
        return Integer.parseInt(dateTime.split("-")[0]);
    }

    public int getMonth() {
        return Integer.parseInt(dateTime.split("-")[1]);
    }

    public int getDay() {
        return Integer.parseInt(dateTime.split("-")[2]);
    }
    public String getCreatedAt() {
        return createdAt;
    }
}