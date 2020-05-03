package com.example.tickit.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Mandate implements Parcelable {

    private String projectName;
    private String position;
    private String startDate;
    private String endDate;
    private int grade;

    public Mandate() { }

    public Mandate(String projectName, String position, String startDate, String endDate, int grade) {
        this.projectName=projectName;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.grade = grade;
    }

    protected Mandate(Parcel in) {
        projectName= in.readString();
        position = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        grade = in.readInt();
    }

    public static final Creator<Mandate> CREATOR = new Creator<Mandate>() {
        @Override
        public Mandate createFromParcel(Parcel in) {
            return new Mandate(in);
        }

        @Override
        public Mandate[] newArray(int size) {
            return new Mandate[size];
        }
    };

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Mandate{" +
                "projectName='" + projectName + '\'' +
                ", position='" + position + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", grade=" + grade +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(projectName);
        dest.writeString(position);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(grade);
    }
}
