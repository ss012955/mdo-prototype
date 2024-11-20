package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.ItemClickListener;
import HelperClasses.ServiceType;
import HelperClasses.Services;

public class ServicesAdapter  extends RecyclerView.Adapter<ServicesAdapter.ServiceHolder>{

    private List<Services> serviceList;

    public static ItemClickListener clickListener;

    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    public ServicesAdapter(List<Services> serviceList){
        this.serviceList = serviceList;
    }


    @NonNull
    @Override
    public ServicesAdapter.ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.servicetype_card,
                parent,
                false);


        return new ServicesAdapter.ServiceHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        Services services = serviceList.get(position);
        holder.title.setText(services.getServiceTitle());

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    public static class ServiceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    //Holds the references of the views within the item layout
    TextView title;



    public ServiceHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.serviceTitle);
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
