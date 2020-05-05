package com.example.tickit.Activities;

import android.content.Context;

import com.example.tickit.Classes.User;

public class GlobalVariables {
    private Context context;
    private User loggedInUser;
    private int userGrade;

    public GlobalVariables(Context context, User loggedInUser) {
        this.context = context;
        this.loggedInUser = loggedInUser;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public int getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(int userGrade) {
        this.userGrade = userGrade;
    }
}
