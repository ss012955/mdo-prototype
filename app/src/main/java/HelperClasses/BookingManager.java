package HelperClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype.R;

import java.util.Calendar;
import java.util.Date;

public class BookingManager {

    private String selectedTimeSlot;
    private Date selectedDate;

    public void showTimeSlotDialog(Context context, OnTimeSlotSelectedListener listener) {
        // Inflate the dialog layout
        View dialogView = View.inflate(context, R.layout.dialog_time, null);

        // Initialize time slots
        TextView[] timeSlots = {
                dialogView.findViewById(R.id.timeSlot1),
                dialogView.findViewById(R.id.timeSlot2),
                dialogView.findViewById(R.id.timeSlot3),
                dialogView.findViewById(R.id.timeSlot4),
                dialogView.findViewById(R.id.timeSlot5),
                dialogView.findViewById(R.id.timeSlot6),
                dialogView.findViewById(R.id.timeSlot7),
                dialogView.findViewById(R.id.timeSlot8)
        };

        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        final String[] selectedTime = {""};

        // Add click listeners to time slots
        View.OnClickListener slotClickListener = view -> {
            for (TextView slot : timeSlots) {
                slot.setBackgroundResource(android.R.color.transparent); // Reset background
            }
            view.setBackgroundResource(R.color.gray); // Highlight selected slot
            selectedTime[0] = ((TextView) view).getText().toString();
        };

        for (TextView timeSlot : timeSlots) {
            timeSlot.setOnClickListener(slotClickListener);
        }

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Confirm button action
        confirmButton.setOnClickListener(v -> {
            if (!selectedTime[0].isEmpty()) {
                selectedTimeSlot = selectedTime[0];
                listener.onTimeSlotSelected(selectedTimeSlot); // Notify listener
            } else {
                Toast.makeText(context, "Please select a time.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    public Date handleCalendarDateSelection(CalendarView calendarView, Context context) {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);

            Calendar today = Calendar.getInstance();
            int selectedDayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);

            if (selectedCalendar.before(today)) {
                Toast.makeText(context, "Please select a future date.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDayOfWeek == Calendar.SATURDAY || selectedDayOfWeek == Calendar.SUNDAY) {
                Toast.makeText(context, "Weekends are not available, please choose a weekday.", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedDate = selectedCalendar.getTime();
            Toast.makeText(context, "Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();
        });
        return selectedDate;
    }


    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(String timeSlot);
    }
}
