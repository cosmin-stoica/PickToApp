package com.example.picktolightapp.Model.User;

import androidx.annotation.NonNull;

public class User {

    private String username;
    private String password;
    private TipoUser tipo;

    public User(){
    }

    public User(TipoUser tipo, String username, String password){
        this.tipo = tipo;
        this.username = username;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTipo(TipoUser tipo) {
        this.tipo = tipo;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public TipoUser getTipo() {
        return tipo;
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
