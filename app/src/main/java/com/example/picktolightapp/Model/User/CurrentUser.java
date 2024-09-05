package com.example.picktolightapp.Model.User;

public class CurrentUser extends User{

    private static CurrentUser instance;
    User user;

    private CurrentUser(){
        this.user = new User();
        this.user.setUsername("none");
        this.user.setPassword("none");
        this.user.setTipo(TipoUser.GUEST);
    }

    private CurrentUser(User user){
        this.user = user;
    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

}
