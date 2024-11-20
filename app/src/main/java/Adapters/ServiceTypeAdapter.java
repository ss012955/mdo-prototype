package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.R;

import java.util.List;

import HelperClasses.ItemClickListener;
import HelperClasses.ServiceType;

public class ServiceTypeAdapter extends  RecyclerView.Adapter<ServiceTypeAdapter.ServiceTypeHolder>{

    private List<ServiceType> serviceTypeList;

    public static ItemClickListener clickListener;

    public void setClickListener(ItemClickListener myListener){
        this.clickListener = myListener;
    }

    public ServiceTypeAdapter(List<ServiceType> serviceTypeList){
        this.serviceTypeList = serviceTypeList;
    }


    @NonNull
    public ServiceTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.service_card,
                parent,
                false);


        return new ServiceTypeHolder(view);
    }

    public void onBindViewHolder(@NonNull ServiceTypeHolder holder, int position) {
        ServiceType serviceType = serviceTypeList.get(position);

        holder.imageView.setImageResource(serviceType.getServiceImage());
        Glide.with(holder.imageView.getContext())
                .load(serviceType.getServiceImage())  // Load image (could be a resource ID or URL)
                .centerCrop()  // Ensures the image is cropped and fills the ImageView
                .into(holder.imageView);

        holder.title.setText(serviceType.getServiceTitle());

    }

    public int getItemCount() {
        return serviceTypeList.size();
    }

    public static class ServiceTypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Holds the references of the views within the item layout
        ImageView imageView;
        TextView title;



        public ServiceTypeHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.serviceImage);
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
