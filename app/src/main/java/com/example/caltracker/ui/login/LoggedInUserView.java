package com.example.caltracker.ui.login;

import com.example.caltracker.RestModel.User;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private User user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;

    }
    LoggedInUserView(User user) {
        this.user = user;
        displayName = user.getName();
    }

    String getDisplayName() {
        return displayName;
    }
    User getUser(){return  user;}
}
