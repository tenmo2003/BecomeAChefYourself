package com.example.test.components;

public class User {
    private String username;
    private String password;
    private String fullname;
    private int points;
    private String bio;

    public User(String username, String password, String fullname, int points, String bio) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.points = points;
        this.bio = bio;
    }

    public User(String username, String fullname, int points, String bio) {
        this.username = username;
        this.fullname = fullname;
        this.points = points;
        this.bio = bio;
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

    public String getFullname() {
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
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
