package Singleton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AvailabilityChecker {
    private static final String API_BASE_URL = "https://umakmdo-91b845374d5b.herokuapp.com/checkbooking_conflict.php";
    private OkHttpClient client;

    // Service durations configuration
    private Map<String, Map<String, Integer>> serviceDurations;
    private Map<String, String> timeSlotMapping;

    public AvailabilityChecker() {
        client = new OkHttpClient();
        initializeServiceDurations();
        initializeTimeSlotMapping();
    }

    private void initializeServiceDurations() {
        serviceDurations = new HashMap<>();

        // Medical Services
        Map<String, Integer> medicalServices = new HashMap<>();
        medicalServices.put("General Consultation", 30);
        medicalServices.put("Physical Examination", 30);
        medicalServices.put("Treatment for minor illness", 30);

        // Dental Services
        Map<String, Integer> dentalServices = new HashMap<>();
        dentalServices.put("Tooth Extraction", 60);
        dentalServices.put("Teeth Cleaning", 60);
        dentalServices.put("Dental Fillings", 60);
        dentalServices.put("Dental Consultation", 30);

        serviceDurations.put("Medical", medicalServices);
        serviceDurations.put("Dental", dentalServices);
    }

    private void initializeTimeSlotMapping() {
        timeSlotMapping = new HashMap<>();
        timeSlotMapping.put("8-9 AM", "08:00:00");
        timeSlotMapping.put("9-10 AM", "09:00:00");
        timeSlotMapping.put("10-11 AM", "10:00:00");
        timeSlotMapping.put("11-12 PM", "11:00:00");
        timeSlotMapping.put("1-2 PM", "13:00:00");
        timeSlotMapping.put("2-3 PM", "14:00:00");
        timeSlotMapping.put("3-4 PM", "15:00:00");
        timeSlotMapping.put("4-5 PM", "16:00:00");
    }

    // Callback interfaces
    public interface AvailabilityCheckCallback {
        void onAvailable(String message);
        void onPartiallyAvailable(String message, String warningMessage);
        void onUnavailable(String message);
        void onError(String error);
    }

    public interface TimeSlotSelectionCallback {
        void onTimeSlotSelected(String timeSlot);
        void onSelectionCancelled();
    }

    // Data class for availability results
    public static class AvailabilityResult {
        public boolean isAvailable;
        public boolean canBook30Min;
        public String message;
        public int serviceDuration;
        public String conflictType;

        public AvailabilityResult(boolean isAvailable, boolean canBook30Min, String message,
                                  int serviceDuration, String conflictType) {
            this.isAvailable = isAvailable;
            this.canBook30Min = canBook30Min;
            this.message = message;
            this.serviceDuration = serviceDuration;
            this.conflictType = conflictType;
        }
    }

    // Get service duration
    public int getServiceDuration(String serviceType, String service) {
        if (serviceDurations.containsKey(serviceType) &&
                serviceDurations.get(serviceType).containsKey(service)) {
            return serviceDurations.get(serviceType).get(service);
        }
        return 30; // Default duration
    }

    // Convert display time to database format
    public String convertToDbTimeFormat(String displayTime) {
        return timeSlotMapping.getOrDefault(displayTime, "08:00:00");
    }

    // Convert database time to display format
    public String convertToDisplayTimeFormat(String dbTime) {
        for (Map.Entry<String, String> entry : timeSlotMapping.entrySet()) {
            if (entry.getValue().equals(dbTime)) {
                return entry.getKey();
            }
        }
        return "8-9 AM"; // Default
    }

    // Check availability for a specific time slot
    public void checkAvailability(String date, String time, String service, String serviceType,
                                  AvailabilityCheckCallback callback) {

        RequestBody formBody = new FormBody.Builder()
                .add("action", "check_availability")
                .add("date", date)
                .add("time", time)
                .add("service", service)
                .add("service_type", serviceType)
                .build();

        Request request = new Request.Builder()
                .url(API_BASE_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        if (json.getBoolean("success")) {
                            boolean available = json.getBoolean("available");
                            boolean canBook30Min = json.getBoolean("canBook30Min");
                            String message = json.getString("message");
                            int serviceDuration = json.getInt("service_duration");
                            String conflictType = json.getString("conflict_type");

                            // Determine callback based on availability
                            if (available) {
                                callback.onAvailable(message);
                            } else if (canBook30Min && serviceDuration == 30) {
                                callback.onPartiallyAvailable(message, "This slot is partially available for 30-minute services only.");
                            } else {
                                callback.onUnavailable(message);
                            }
                        } else {
                            callback.onError(json.getString("message"));
                        }
                    } catch (JSONException e) {
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }
        });
    }

    // FIXED: Show time slot selection dialog with pre-validation
    public void showAvailabilityAwareTimeSlotDialog(Context context, String selectedDate,
                                                    String service, String serviceType,
                                                    TimeSlotSelectionCallback callback) {

        String[] timeSlots = {
                "8-9 AM", "9-10 AM", "10-11 AM", "11-12 PM",
                "1-2 PM", "2-3 PM", "3-4 PM", "4-5 PM"
        };

        // Show loading dialog while checking all slots
        ProgressDialog loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage("Checking availability for all time slots...");
        loadingDialog.show();

        // Check availability for all time slots first
        checkAllTimeSlotsAvailability(selectedDate, service, serviceType, timeSlots,
                new AllSlotsCheckCallback() {
                    @Override
                    public void onAllSlotsChecked(Map<String, SlotAvailability> availabilityMap) {
                        ((Activity) context).runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            showTimeSlotDialogWithAvailability(context, timeSlots, availabilityMap, callback);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        ((Activity) context).runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            Toast.makeText(context, "Error checking availability: " + error, Toast.LENGTH_LONG).show();
                            // Show dialog anyway but without availability info
                            showBasicTimeSlotDialog(context, timeSlots, callback);
                        });
                    }
                });
    }

    // Helper class for slot availability
    public static class SlotAvailability {
        public boolean isAvailable;
        public boolean canBook30Min;
        public String message;
        public String status; // "AVAILABLE", "PARTIAL", "UNAVAILABLE"

        public SlotAvailability(boolean isAvailable, boolean canBook30Min, String message, String status) {
            this.isAvailable = isAvailable;
            this.canBook30Min = canBook30Min;
            this.message = message;
            this.status = status;
        }
    }

    // Interface for checking all slots
    public interface AllSlotsCheckCallback {
        void onAllSlotsChecked(Map<String, SlotAvailability> availabilityMap);
        void onError(String error);
    }

    // Check availability for all time slots
    private void checkAllTimeSlotsAvailability(String date, String service, String serviceType,
                                               String[] timeSlots, AllSlotsCheckCallback callback) {
        Map<String, SlotAvailability> availabilityMap = new HashMap<>();
        final int[] completedChecks = {0};
        final int totalChecks = timeSlots.length;

        for (String timeSlot : timeSlots) {
            String dbTime = convertToDbTimeFormat(timeSlot);
            checkAvailability(date, dbTime, service, serviceType, new AvailabilityCheckCallback() {
                @Override
                public void onAvailable(String message) {
                    synchronized (availabilityMap) {
                        availabilityMap.put(timeSlot, new SlotAvailability(true, true, message, "AVAILABLE"));
                        completedChecks[0]++;
                        if (completedChecks[0] == totalChecks) {
                            callback.onAllSlotsChecked(availabilityMap);
                        }
                    }
                }

                @Override
                public void onPartiallyAvailable(String message, String warningMessage) {
                    synchronized (availabilityMap) {
                        availabilityMap.put(timeSlot, new SlotAvailability(false, true, message, "PARTIAL"));
                        completedChecks[0]++;
                        if (completedChecks[0] == totalChecks) {
                            callback.onAllSlotsChecked(availabilityMap);
                        }
                    }
                }

                @Override
                public void onUnavailable(String message) {
                    synchronized (availabilityMap) {
                        availabilityMap.put(timeSlot, new SlotAvailability(false, false, message, "UNAVAILABLE"));
                        completedChecks[0]++;
                        if (completedChecks[0] == totalChecks) {
                            callback.onAllSlotsChecked(availabilityMap);
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    synchronized (availabilityMap) {
                        availabilityMap.put(timeSlot, new SlotAvailability(false, false, "Error checking", "ERROR"));
                        completedChecks[0]++;
                        if (completedChecks[0] == totalChecks) {
                            callback.onAllSlotsChecked(availabilityMap);
                        }
                    }
                }
            });
        }
    }

    // Show time slot dialog with availability indicators
    private void showTimeSlotDialogWithAvailability(Context context, String[] timeSlots,
                                                    Map<String, SlotAvailability> availabilityMap,
                                                    TimeSlotSelectionCallback callback) {

        // Create display items with availability status
        String[] displayItems = new String[timeSlots.length];
        for (int i = 0; i < timeSlots.length; i++) {
            String timeSlot = timeSlots[i];
            SlotAvailability availability = availabilityMap.get(timeSlot);

            if (availability != null) {
                switch (availability.status) {
                    case "AVAILABLE":
                        displayItems[i] = "✅ " + timeSlot + " - Available";
                        break;
                    case "PARTIAL":
                        displayItems[i] = "⚠️ " + timeSlot + " - Partially Available (30 min only)";
                        break;
                    case "UNAVAILABLE":
                        displayItems[i] = "❌ " + timeSlot + " - Unavailable";
                        break;
                    default:
                        displayItems[i] = "❓ " + timeSlot + " - Status Unknown";
                        break;
                }
            } else {
                displayItems[i] = "❓ " + timeSlot + " - Status Unknown";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Time Slot");

        builder.setItems(displayItems, (dialog, which) -> {
            String selectedTimeSlot = timeSlots[which];
            SlotAvailability availability = availabilityMap.get(selectedTimeSlot);

            if (availability == null || availability.status.equals("ERROR")) {
                Toast.makeText(context, "Unable to verify availability for this slot", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (availability.status) {
                case "AVAILABLE":
                    callback.onTimeSlotSelected(selectedTimeSlot);
                    break;

                case "PARTIAL":
                    showPartialAvailabilityDialog(context, selectedTimeSlot, availability.message, callback);
                    break;

                case "UNAVAILABLE":
                    showUnavailableDialog(context, availability.message);
                    break;

                default:
                    Toast.makeText(context, "Please try selecting another time slot", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> callback.onSelectionCancelled());
        builder.show();
    }

    // Fallback basic dialog without availability checking
    private void showBasicTimeSlotDialog(Context context, String[] timeSlots, TimeSlotSelectionCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Time Slot");
        builder.setItems(timeSlots, (dialog, which) -> callback.onTimeSlotSelected(timeSlots[which]));
        builder.setNegativeButton("Cancel", (dialog, which) -> callback.onSelectionCancelled());
        builder.show();
    }

    private void showPartialAvailabilityDialog(Context context, String timeSlot, String message,
                                               TimeSlotSelectionCallback callback) {
        new AlertDialog.Builder(context)
                .setTitle("⚠️ Partial Availability")
                .setMessage(message + "\n\nThis time slot is partially booked. You can book a 30-minute service, but consider choosing another time for better availability.\n\nDo you want to proceed with " + timeSlot + "?")
                .setPositiveButton("Yes, Continue", (dialog, which) ->
                        callback.onTimeSlotSelected(timeSlot))
                .setNegativeButton("Choose Another Time", (dialog, which) ->
                        callback.onSelectionCancelled())
                .show();
    }

    private void showUnavailableDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("❌ Time Slot Unavailable")
                .setMessage(message + "\n\nPlease select a different time slot.")
                .setPositiveButton("OK", null)
                .show();
    }

    // Quick availability check (for validation before proceeding)
    public void validateTimeSlotAvailability(String date, String time, String service, String serviceType,
                                             ValidationCallback callback) {
        String dbTimeFormat = convertToDbTimeFormat(time);

        checkAvailability(date, dbTimeFormat, service, serviceType, new AvailabilityCheckCallback() {
            @Override
            public void onAvailable(String message) {
                callback.onValidationPassed(message);
            }

            @Override
            public void onPartiallyAvailable(String message, String warningMessage) {
                callback.onValidationPassedWithWarning(message, warningMessage);
            }

            @Override
            public void onUnavailable(String message) {
                callback.onValidationFailed(message);
            }

            @Override
            public void onError(String error) {
                callback.onValidationError(error);
            }
        });
    }

    public interface ValidationCallback {
        void onValidationPassed(String message);
        void onValidationPassedWithWarning(String message, String warning);
        void onValidationFailed(String reason);
        void onValidationError(String error);
    }

    // Helper method for simple availability checking with toast
    public void checkAndToastAvailability(Context context, String date, String timeSlot, String service, String serviceType) {
        String dbTime = convertToDbTimeFormat(timeSlot);

        checkAvailability(date, dbTime, service, serviceType, new AvailabilityCheckCallback() {
            @Override
            public void onAvailable(String message) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "✅ Available: " + message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onPartiallyAvailable(String message, String warningMessage) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "⚠️ Partial: " + warningMessage, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onUnavailable(String message) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "❌ Unavailable: " + message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}