package com.example.prototype;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.util.List;

import HelperClasses.FetchImageTask;
import HelperClasses.ProfileClass;

public class Profile_CustomAdapter extends RecyclerView.Adapter<Profile_CustomAdapter.ProfileViewHolder> {

    private final List<ProfileClass> profileList;
    private final OnEditButtonClickListener editButtonClickListener;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Activity activity;
    private int selectedPosition = -1;
    private ActivityResultLauncher<Intent> imagePickerLauncher; // New field
    private boolean isImageClickable;  // Flag to control image clickability
    private  Fragment fragment;
    // Constructor
    public Profile_CustomAdapter(List<ProfileClass> profileList, OnEditButtonClickListener listener, Activity activity, ActivityResultLauncher<Intent> imagePickerLauncher, boolean isImageClickable) {
        this.profileList = profileList;
        this.editButtonClickListener = listener;
        this.activity = activity;
        this.imagePickerLauncher = imagePickerLauncher;
        this.isImageClickable = isImageClickable;
    }

    public Profile_CustomAdapter(List<ProfileClass> profileList, OnEditButtonClickListener listener, fProfile fProfile, ActivityResultLauncher<Intent> imagePickerLauncher) {
        this(profileList, listener, fProfile.getActivity(), imagePickerLauncher, false);
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

        // Log to check if the position is correct
        Log.e("Profile_CustomAdapter", "onBindViewHolder - Position: " + position);

        new FetchImageTask().fetchImage(profile.getEmail(), holder.profile_Image);


        holder.studentID.setText(profile.getStudentID());
        holder.email.setText(profile.getEmail());
        holder.name.setText(profile.getName());

        // Handle click to open file chooser
        if (isImageClickable) {
            holder.profile_Image.setOnClickListener(v -> openFileChooser(position));
        } else {
            holder.profile_Image.setClickable(false);  // Disable click event
        }

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
    private void openFileChooser(int position) {
        Log.d("Profile_CustomAdapter", "openFileChooser - Position: " + position);
        Log.d("Profile_CustomAdapter", "openFileChooser - Selected Position: " + selectedPosition);

        if (position >= 0 && position < profileList.size()) {
            selectedPosition = position;  // Set selectedPosition correctly
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent); // Launch the image picker intent
        } else {
            // Handle invalid position case
            Toast.makeText(activity, "Invalid position", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateImageAtPosition(Uri imageUri, int position) {
        if (position >= 0 && position < profileList.size()) {
            ProfileClass profile = profileList.get(position);
            profile.setProfile_ImageUri(imageUri); // Assuming ProfileClass has this method
            notifyItemChanged(position);  // Notify the adapter to update the UI
        } else {
            // Handle invalid position case
            Toast.makeText(activity, "Invalid position", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getSelectedImageUri() {
        if (selectedPosition >= 0 && selectedPosition < profileList.size()) {
            ProfileClass profile = profileList.get(selectedPosition);
            return profile.getProfileImageUri();  // Assuming `ProfileClass` has this method
        } else {
            return null; // Return null if no valid selection exists
        }
    }

}
