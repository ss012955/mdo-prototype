package HelperClasses;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Set;

public class EventDecoratorApproved implements DayViewDecorator {
    private final Set<CalendarDay> dates;

    public EventDecoratorApproved(Set<CalendarDay> dates) {
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new ForegroundColorSpan(Color.BLACK));
        // Create a circular drawable for the background
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL); // Circular shape
        circleDrawable.setColor(Color.parseColor("#4BB543"));  // Set the background color
        circleDrawable.setSize(50, 50); // Optional size (to ensure consistency)

        // Apply the circular background
        view.setBackgroundDrawable(circleDrawable);
    }
}