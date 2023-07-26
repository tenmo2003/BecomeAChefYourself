package com.example.test.components;

public class User {
    private String username;
    private String password;
    private String fullname;
    private int points;
    private String bio;
    private String avatarURL;
    private int banned;
    private int reportLevel;

    public User(String username, String password, String fullname, int points, String bio, String avatarURL, int banned, int reportLevel) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.points = points;
        this.bio = bio;
        if (avatarURL == null) {
            this.avatarURL = "";
        } else {
            this.avatarURL = avatarURL;
        }
        this.banned = banned;
        this.reportLevel = reportLevel;
    }

    public User(String username, String fullname, int points, String bio, String avatarURL, int banned, int reportLevel) {
        this.username = username;
        this.fullname = fullname;
        this.points = points;
        this.bio = bio;
        if (avatarURL == null) {
            this.avatarURL = "";
        } else {
            this.avatarURL = avatarURL;
        }
        this.banned = banned;
        this.reportLevel = reportLevel;
    }

    //getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getFullname() {
        if (fullname == null) {
            return "";
        }
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getBio() {
        if (bio == null) {
            return "";
        }
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getBanned() {
        return banned;
    }

    public void setBanned(int banned) {
        this.banned = banned;
    }

    public int getReportLevel() {
        return reportLevel;
    }

    public void setReportLevel(int reportLevel) {
        this.reportLevel = reportLevel;
    }
}
