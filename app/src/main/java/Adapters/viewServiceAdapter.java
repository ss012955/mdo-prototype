package Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.Services;

public class viewServiceAdapter extends RecyclerView.Adapter<viewServiceAdapter.ViewHolder> {
    private List<Services> services;

    public viewServiceAdapter(List<Services> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public viewServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.servicetype_card, parent, false);
        return new viewServiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Services service = services.get(position);
        holder.serviceName.setText(service.getServiceTitle());
        Log.d("ViewServiceAdapter", "Binding service: " + service.getServiceTitle());
    }

    @Override
    public int getItemCount() {
        Log.d("RecyclerView", "Item count: " + services.size());
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView serviceName;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceTitle);
        }
    }
}
