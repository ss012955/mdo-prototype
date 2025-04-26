package Adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        // Check if the message sender is the admin
        if (message.getSenderEmail().equals("admin2@example.com")) {
            // Change the background color for admin messages
            holder.chatLinear.setBackgroundResource(R.drawable.roundedchat_admin);
            holder.messageTextView.setTextColor(Color.BLACK);
        } else {
            // Default background for user messages
            holder.chatLinear.setBackgroundResource(R.drawable.rounded_chat);
            holder.messageTextView.setTextColor(Color.WHITE);
        }

        // Set the message text
        holder.messageTextView.setText(message.getMessage());
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
        private final LinearLayout chatLinear;  // Declare chatLinear here
        private final LinearLayout chatDetails;  // Declare chatLinear here
        private final LinearLayout timeDetails;  // Declare chatLinear here

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            senderTextView = itemView.findViewById(R.id.message_sender);
            timestampTextView = itemView.findViewById(R.id.message_timestamp);
            chatLinear = itemView.findViewById(R.id.chatLinear);
            chatDetails = itemView.findViewById(R.id.chatDetails);
            timeDetails = itemView.findViewById(R.id.timeDetails);


        }

        public void bind(Message message) {
            messageTextView.setText(message.getMessage());
            senderTextView.setText(message.getSenderEmail());
            Date messageDate = new Date(message.getTimestamp());
            Date currentDate = new Date();


            if ("admin2@example.com".equals(message.getSenderEmail())) {
                // Set the sender's text and make it visible
                senderTextView.setText("MDO Admin");
                senderTextView.setVisibility(View.VISIBLE);

                // Align the message bubble and timestamp to the start (left)
                chatLinear.setGravity(Gravity.START);   // Align the message bubble to the left
                chatDetails.setGravity(Gravity.START);  // Align the whole chat details to the left

                // Align sender's name and timestamp to the left
                senderTextView.setGravity(Gravity.START);
                timestampTextView.setForegroundGravity(Gravity.START);

                // Set the background for the admin's message
                chatLinear.setBackgroundResource(R.drawable.roundedchat_admin);
            }else if ("mdo.agent@system.com".equals(message.getSenderEmail())) {
                // Set the sender's text and make it visible
                senderTextView.setText("MDO Agent");
                senderTextView.setVisibility(View.VISIBLE);

                // Align the message bubble and timestamp to the start (left)
                chatLinear.setGravity(Gravity.START);   // Align the message bubble to the left
                chatDetails.setGravity(Gravity.START);  // Align the whole chat details to the left

                // Align sender's name and timestamp to the left
                senderTextView.setGravity(Gravity.START);
                timestampTextView.setForegroundGravity(Gravity.START);

                // Set the background for the admin's message
                chatLinear.setBackgroundResource(R.drawable.roundedchat_admin);

            } else {
                chatLinear.setGravity(Gravity.END);   // Align the message bubble to the right
                chatDetails.setGravity(Gravity.END);  // Align the whole chat details to the right

                // Align sender's name and timestamp to the right
                senderTextView.setGravity(Gravity.END);
                timestampTextView.setForegroundGravity(Gravity.START);

                // Set the background for the normal message
                chatLinear.setBackgroundResource(R.drawable.rounded_chat);
            }


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
