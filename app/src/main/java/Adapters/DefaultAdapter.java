package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

public class DefaultAdapter extends RecyclerView.Adapter<DefaultAdapter.DefaultViewHolder> {
    private String message;

    public DefaultAdapter(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default_message, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.messageTextView.setText(message);
    }

    @Override
    public int getItemCount() {
        return 1; // Only one item for the default message
    }

    public static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
