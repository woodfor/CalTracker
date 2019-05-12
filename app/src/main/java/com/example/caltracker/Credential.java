package com.example.caltracker;

import java.util.Date;

public class Credential {
    private Integer id;
    private String username;
    private String sha256;
    private Date signupdate;
    private User uid;

    public Credential( String username, String sha256, Date signupdate, User uid) {
        this.username = username;
        this.sha256 = sha256;
        this.signupdate = signupdate;
        this.uid = uid;
    }

    public  Credential(){

    }

}
