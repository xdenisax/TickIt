package com.example.tickit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Edition implements Parcelable {
    private User coordinator1;
    private User coordinator2;
    private ArrayList<User> members;
    private String strategy;
    private String year;

    public Edition(){}

    public Edition(User coordinator1, User coordinator2, ArrayList<User> members, String strategy, String year) {
        this.coordinator1 = coordinator1;
        this.coordinator2 = coordinator2;
        this.members = members;
        this.strategy = strategy;
        this.year = year;
    }

    protected Edition(Parcel in) {
        coordinator1 = in.readParcelable(User.class.getClassLoader());
        coordinator2 = in.readParcelable(User.class.getClassLoader());
        members = in.createTypedArrayList(User.CREATOR);
        strategy = in.readString();
        year = in.readString();
    }

    public static final Creator<Edition> CREATOR = new Creator<Edition>() {
        @Override
        public Edition createFromParcel(Parcel in) {
            return new Edition(in);
        }

        @Override
        public Edition[] newArray(int size) {
            return new Edition[size];
        }
    };

    public User getCoordinator1() {
        return coordinator1;
    }

    public void setCoordinator1(User coordinator1) {
        this.coordinator1 = coordinator1;
    }

    public User getCoordinator2() {
        return coordinator2;
    }

    public void setCoordinator2(User coordinator2) {
        this.coordinator2 = coordinator2;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Edition{" +
                "coordinator1=" + coordinator1 +
                ", coordinator2=" + coordinator2 +
                ", members=" + members +
                ", strategy='" + strategy + '\'' +
                ", year=" + year +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(coordinator1, flags);
        dest.writeParcelable(coordinator2, flags);
        dest.writeTypedList(members);
        dest.writeString(strategy);
        dest.writeString(year);
    }
}
