package com.example.tickit;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;

public class User implements Parcelable {
    String id;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    String profilePicture;

    public User(String id, String firstName, String lastName, String phoneNumber, String email, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    protected User(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        profilePicture = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture=" + profilePicture +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(profilePicture);
    }
}
