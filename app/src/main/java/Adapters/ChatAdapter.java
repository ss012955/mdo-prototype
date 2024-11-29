package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Singleton.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Message> messages = new ArrayList<>();

    // Constructor to initialize the messages list
    public ChatAdapter(List<Message> messages) {
        this.messages = messages; // Fix: use 'this.messages' to correctly set the list
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged(); // Notify the adapter when the data is updated
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final TextView senderTextView;
        private final TextView timestampTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            senderTextView = itemView.findViewById(R.id.message_sender);
            timestampTextView = itemView.findViewById(R.id.message_timestamp);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getMessage());
            senderTextView.setText(message.getSender());
            Date messageDate = new Date(message.getTimestamp());
            Date currentDate = new Date();

            // Format the timestamp for today (only show time)
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

            // If the message was sent today, only show the time
            if (isSameDay(messageDate, currentDate)) {
                timestampTextView.setText(timeFormat.format(messageDate));
            } else {
                // If the message was sent on a different day, show the full date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy, h:mm a", Locale.getDefault());
                timestampTextView.setText(dateFormat.format(messageDate));
            }

            // Set OnClickListener to show the date in a Toast when clicked
            messageTextView.setOnClickListener(v -> {
                String toastMessage = "Sent on: " + timeFormat.format(messageDate);
                Toast.makeText(v.getContext(), toastMessage, Toast.LENGTH_LONG).show();
            });
        }

        // Helper method to check if two dates are on the same day
        private boolean isSameDay(Date date1, Date date2) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            return dayFormat.format(date1).equals(dayFormat.format(date2));
        }
    }
}
