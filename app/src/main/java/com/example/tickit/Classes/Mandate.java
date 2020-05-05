package com.example.tickit.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Mandate implements Parcelable {

    private DocumentReference project_name;
    private String position;
    private Date start_date;
    private Date stop_date;
    private int grade;


    public Mandate() { }

    public Mandate(DocumentReference projectName, String position, Date startDate, Date endDate, int grade) {
        this.project_name =projectName;
        this.position = position;
        this.start_date = startDate;
        this.stop_date = endDate;
        this.grade = grade;
    }


    protected Mandate(Parcel in) {
        project_name = FirebaseFirestore.getInstance().document((String) in.readSerializable());
        position = in.readString();
        grade = in.readInt();
        start_date = (Date) in.readSerializable();
        stop_date = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(project_name.getPath());
        dest.writeString(position);
        dest.writeInt(grade);
        dest.writeSerializable(start_date);
        dest.writeSerializable(stop_date);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public DocumentReference getProject_name() {
        return project_name;
    }

    public void setProject_name(DocumentReference project_name) {
        this.project_name = project_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getStop_date() {
        return stop_date;
    }

    public void setStop_date(Date stop_date) {
        this.stop_date = stop_date;
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
                "projectName='" + project_name + '\'' +
                ", position='" + position + '\'' +
                ", startDate='" + start_date + '\'' +
                ", endDate='" + stop_date + '\'' +
                ", grade=" + grade +
                '}';
    }


}
