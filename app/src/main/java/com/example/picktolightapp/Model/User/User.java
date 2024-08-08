package com.example.picktolightapp.Model.User;

import androidx.annotation.NonNull;

public class User {

    private static User instance;
    private String username;
    private String password;

    public User(){
    }

    private User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
