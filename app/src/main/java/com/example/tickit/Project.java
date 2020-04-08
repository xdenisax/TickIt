package com.example.tickit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

class Project implements Parcelable {
    private String name;
    private String description;
    private String imageLink;
    private ArrayList<Edition> editions;

    public Project() { }

    public Project(String name, String description, String imageLink, ArrayList<Edition> editions) {
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
        this.editions = editions;
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
        return name;
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
