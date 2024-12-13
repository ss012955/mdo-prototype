package HelperClasses;

import android.net.Uri;

public class ProfileClass {
    private Uri profileImageUri;
    private int profile_Image;
    private String studentID, email, name;


    public ProfileClass(int profile_Image, String studentID, String email, String name) {
        this.profile_Image = profile_Image;
        this.studentID = studentID;
        this.email = email;
        this.name = name;
    }

    public ProfileClass(Uri profileImageUri, String studentID, String email, String name) {
        this.profileImageUri = profileImageUri;
        this.studentID = studentID;
        this.email = email;
        this.name = name;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfile_ImageUri(Uri uri) {
        this.profileImageUri = uri;
    }

    public int getProfile_Image() {
        return profile_Image;
    }

    public void setProfile_Image(int profile_Image) {
        this.profile_Image = profile_Image;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
