package com.example.tickit.Classes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Project implements Parcelable {
    private String name;
    private String description;
    private String imageLink;
    private ArrayList<Edition> editions;

    public Project() { }

    public Project(final String Name, final String Description, final String imgLink, final ArrayList<Edition> Editions) {
        getPhotoUri(imgLink, new CallbackString(){
            @Override
            public void onCallBack(String value) {
                name = Name;
                description = Description;
                imageLink=value;
                editions = Editions;
            }
        });
    }

    private void getPhotoUri(String imageLink, final CallbackString callbackString){
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mImageStorage.child("TickIt").child("ProjectsLogo").child(imageLink);

        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    String imageUrl = downUri.toString();
                    callbackString.onCallBack(imageUrl);
                }
            }
        });

    }
    protected Project(Parcel in) {
        name = in.readString();
        description = in.readString();
        imageLink = in.readString();
        editions = in.createTypedArrayList(Edition.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public ArrayList<Edition> getEditions() {
        return editions;
    }

    public void setEditions(ArrayList<Edition> editions) {
        this.editions = editions;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", editions=" + editions +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageLink);
        dest.writeTypedList(editions);
    }
}
