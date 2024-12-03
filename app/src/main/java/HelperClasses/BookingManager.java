package HelperClasses;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.prototype.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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

    public Date handleCalendarDateSelection(MaterialCalendarView calendarView, Context context) {
        // Set the OnDateChangedListener for MaterialCalendarView
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = null;
                // Create a Calendar instance with the selected date
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(date.getYear(), date.getMonth() - 1, date.getDay()); // Month is 0-based

                // Get today's date to compare
                Calendar today = Calendar.getInstance();

                // Check if the selected date is in the past
                if (selectedCalendar.before(today)) {
                    Toast.makeText(context, "Please select a future date.", Toast.LENGTH_SHORT).show();
                    return; // Exit if the date is in the past
                }

                // Check if the selected date is a weekend (Saturday or Sunday)
                int selectedDayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);
                if (selectedDayOfWeek == Calendar.SATURDAY || selectedDayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Weekends are not available, please choose a weekday.", Toast.LENGTH_SHORT).show();
                    return; // Exit if the date is a weekend
                }

                // Update the selectedDate only if it's valid
                selectedDate = selectedCalendar.getTime();
                Toast.makeText(context, "Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        return selectedDate; // Return the selected date (or null if no date is selected yet)
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

    public static void smoothProgressUpdate(ProgressBar progressBar, int targetProgress) {
        // Ensure the target progress is within valid bounds
        final int finalTargetProgress = Math.max(0, Math.min(targetProgress, progressBar.getMax()));

        final int[] currentProgress = {progressBar.getProgress()}; // Start from current progress

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentProgress[0] < finalTargetProgress) {
                    currentProgress[0] += 3; // Increment by 1 for smoother animation
                    progressBar.setProgress(currentProgress[0]); // Update progress
                    handler.postDelayed(this, 15); // Short delay for smooth effect
                } else {
                    // Ensure the progress stops exactly at target
                    progressBar.setProgress(finalTargetProgress);
                }
            }
        }, 15);
    }


    public void animateProgressBar(ProgressBar progressBar, int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(700); // Smooth animation duration
        animation.start();
    }
}