package HelperClasses;

public class AppointmentDaysClass {
    private  String dateTime;

    public AppointmentDaysClass(String dateTime){
        this.dateTime = dateTime;
    }
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    // Method to extract the year from the dateTime string
    public int getYear() {
        return Integer.parseInt(dateTime.split("-")[0]);
    }

    // Method to extract the month from the dateTime string
    public int getMonth() {
        return Integer.parseInt(dateTime.split("-")[1]);
    }

    // Method to extract the day from the dateTime string
    public int getDay() {
        return Integer.parseInt(dateTime.split("-")[2]);
    }
}
