package com.example.tickit.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class ProjectTask implements Parcelable {
    private String id;
    private String division;
    private String taskName;
    private DocumentReference project;
    private Date startDate;
    private Date stopDate;
    private int numberOfVolunteers;
    private String taskResource;
    private String taskDescription;
    private ArrayList<AssumedTasksSituation> membersWhoAssumed;

    public  ProjectTask(){}

    public ProjectTask(String id, String taskName, String taskDescription, DocumentReference project, String division, Date startDate, Date stopDate, int numberOfVolunteers, String taskResource, ArrayList<AssumedTasksSituation> membersWhoAssumed) {
        this.id = id;
        this.taskName = taskName;
        this.project = project;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.numberOfVolunteers = numberOfVolunteers;
        this.taskResource = taskResource;
        this.membersWhoAssumed = membersWhoAssumed;
        this.division=division;
        this.taskDescription=taskDescription;
    }

    protected ProjectTask(Parcel in) {
        id = in.readString();
        division = in.readString();
        taskName = in.readString();
        numberOfVolunteers = in.readInt();
        taskResource = in.readString();
        taskDescription = in.readString();
        membersWhoAssumed = in.createTypedArrayList(AssumedTasksSituation.CREATOR);
        startDate = (Date) in.readSerializable();
        stopDate = (Date) in.readSerializable();
        project = FirebaseFirestore.getInstance().document((String) in.readSerializable());
    }

    public static final Creator<ProjectTask> CREATOR = new Creator<ProjectTask>() {
        @Override
        public ProjectTask createFromParcel(Parcel in) {
            return new ProjectTask(in);
        }

        @Override
        public ProjectTask[] newArray(int size) {
            return new ProjectTask[size];
        }
    };

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public DocumentReference getProject() {
        return project;
    }

    public void setProject(DocumentReference project) {
        this.project = project;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public int getNumberOfVolunteers() {
        return numberOfVolunteers;
    }

    public void setNumberOfVolunteers(int numberOfVolunteers) {
        this.numberOfVolunteers = numberOfVolunteers;
    }

    public String getTaskResource() {
        return taskResource;
    }

    public void setTaskResource(String taskResource) {
        this.taskResource = taskResource;
    }

    public ArrayList<AssumedTasksSituation> getMembersWhoAssumed() {
        return membersWhoAssumed;
    }

    public void setMembersWhoAssumed(ArrayList<AssumedTasksSituation> membersWhoAssumed) {
        this.membersWhoAssumed = membersWhoAssumed;
    }

    @Override
    public String toString() {
        return "ProjectTask{" +
                "id='" + id + '\'' +
                ", division='" + division + '\'' +
                ", taskName='" + taskName + '\'' +
                ", description='" + taskDescription + '\'' +
                ", project=" + project +
                ", startDate=" + startDate +
                ", stopDate=" + stopDate +
                ", numberOfVolunteers=" + numberOfVolunteers +
                ", taskResource='" + taskResource + '\'' +
                ", membersWhoAssumed=" + membersWhoAssumed +
                '}';
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(division);
        dest.writeString(taskName);
        dest.writeInt(numberOfVolunteers);
        dest.writeString(taskResource);
        dest.writeString(taskDescription);
        dest.writeTypedList(membersWhoAssumed);
        dest.writeSerializable(startDate);
        dest.writeSerializable(stopDate);
        dest.writeSerializable(project.getPath());
    }
}
