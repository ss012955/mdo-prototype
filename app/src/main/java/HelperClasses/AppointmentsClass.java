package HelperClasses;


public class AppointmentsClass {

    private  String bookingID;
    private String status;
    private String appointNumber;
    private String service;
    private String dateTime;
    private String remarks;
    private String createdAt;
    public AppointmentsClass(String bookingID, String status, String appointNumber, String service, String dateTime, String remarks, String createdAt) {
        this.bookingID = bookingID;
        this.status = status;
        this.appointNumber = appointNumber;
        this.service = service;
        this.dateTime = dateTime;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppointNumber() {
        return appointNumber;
    }

    public void setAppointNumber(String appointNumber) {
        this.appointNumber = appointNumber;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    // Getter for createdAt
    public String getCreatedAt() {
        return createdAt;
    }

    // Setter for createdAt
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}