package Adapters;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.AppointmentsClass;
import HelperClasses.ItemClickListener;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsHolder> {
    public static ItemClickListener clickListener;
    private List<AppointmentsClass> appointmentsList;

    public AppointmentsAdapter(List<AppointmentsClass> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }


    @NonNull
    @Override
    public AppointmentsAdapter.AppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.appointment_card,
                parent,
                false);


        return new AppointmentsAdapter.AppointmentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsAdapter.AppointmentsHolder holder, int position) {
        AppointmentsClass appointments = appointmentsList.get(position);
        holder.tvappointmentNumber.setText(appointments.getAppointNumber());
        holder.tvStatus.setText(appointments.getStatus());
        holder.tvService.setText(appointments.getService());
        holder.tvDateTime.setText(appointments.getDateTime());
        holder.tvRemarks.setText(appointments.getRemarks());

        String status = appointments.getStatus();
        if ("Pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundColor(Color.YELLOW);
        } else if ("Approved".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#4BB543"));
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundColor(Color.RED);
        } else {
            holder.tvStatus.setBackgroundColor(Color.GRAY); // Default for unexpected statuses
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position); // Pass the clicked item's position
            }
        });

    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    public static class AppointmentsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Holds the references of the views within the item layout
        TextView tvappointmentNumber, tvStatus, tvService, tvDateTime, tvRemarks;



        public AppointmentsHolder(@NonNull View itemView) {
            super(itemView);
            tvappointmentNumber = itemView.findViewById(R.id.tvappointmentNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvService = itemView.findViewById(R.id.tvService);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.onClick(v, getBindingAdapterPosition());
            }
        }
    }
}