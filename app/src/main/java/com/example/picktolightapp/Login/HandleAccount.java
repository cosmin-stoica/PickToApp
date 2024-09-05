package com.example.picktolightapp.Login;

import android.content.Context;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.Model.User.TipoUser;
import com.example.picktolightapp.Model.User.User;
import com.example.picktolightapp.R;

public class HandleAccount {

    public static void logoutUser(Context context){
        GlobalVariables.getInstance().setLastOptionGroupName("Main");
        String[] settingsOptions = context.getResources().getStringArray(R.array.settings_options);
        GlobalVariables.getInstance().setLastUserManagementOptions(settingsOptions);
        CurrentUser.getInstance().setUsername("Guest");
        CurrentUser.getInstance().setPassword("");
        CurrentUser.getInstance().setTipo(TipoUser.GUEST);
        MainActivity.goToLogin();
    }

}
