package com.example.tickit;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;

public class ProjectTask {
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
}
