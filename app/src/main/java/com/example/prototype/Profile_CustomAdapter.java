package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import HelperClasses.ProfileClass;

public class Profile_CustomAdapter extends RecyclerView.Adapter<Profile_CustomAdapter.ProfileViewHolder> {

    private final List<ProfileClass> profileList;
    private final OnEditButtonClickListener editButtonClickListener;

    // Constructor
    public Profile_CustomAdapter(List<ProfileClass> profileList, OnEditButtonClickListener listener) {
        this.profileList = profileList;
        this.editButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_profile_item,
                parent,
                false
        );
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileClass profile = profileList.get(position);

        // Set profile data to views
        holder.profile_Image.setImageResource(profile.getProfile_Image());
        holder.studentID.setText(profile.getStudentID());
        holder.email.setText(profile.getEmail());
        holder.name.setText(profile.getName());

        // Handle click on edit button
        holder.editbtn.setOnClickListener(v -> {
            if (editButtonClickListener != null) {
                editButtonClickListener.onEditButtonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    // Define view holder
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_Image;
        TextView studentID, email, name;
        ImageButton editbtn;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profile_Image = itemView.findViewById(R.id.imageview_profilepicture);
            studentID = itemView.findViewById(R.id.textview_studentid);
            email = itemView.findViewById(R.id.textview_email);
            name = itemView.findViewById(R.id.textview_name);
            editbtn = itemView.findViewById(R.id.editprofile_button);
        }
    }

    // Interface for handling edit button clicks
    public interface OnEditButtonClickListener {
        void onEditButtonClick(int position);
    }
}
