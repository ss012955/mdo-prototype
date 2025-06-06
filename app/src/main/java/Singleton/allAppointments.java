package Singleton;

import java.util.List;

import HelperClasses.AppointmentsClass;

public class allAppointments {
    private static allAppointments instance;
    private int numberOfAppointments;
    private List<AppointmentsClass> appointmentsList;
    // Private constructor to prevent instantiation
    private allAppointments() {}

    // Get the singleton instance
    public static allAppointments getInstance() {
        if (instance == null) {
            instance = new allAppointments();
        }
        return instance;
    }

    // Get the number of appointments
    public int getNumberOfAppointments() {
        return numberOfAppointments;
    }

    // Set the number of appointments
    public void setNumberOfAppointments(int numberOfAppointments) {
        this.numberOfAppointments = numberOfAppointments;
    }
}