package com.example.picktolightapp.Toolbar;

import android.app.Activity;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.R;

import org.w3c.dom.Text;

public class ButtonHandler {

    private Activity activity;
    static TextView accountBtn;

    public ButtonHandler(Activity activity) {
        this.activity = activity;
    }

    public void setButtonListeners(ImageButton settingsBtn, ImageButton notificheButton, TextView accountBtn, ImageView logo, LinearLayout IndietroBtn) {
        this.accountBtn = accountBtn;
        handleAggiornaToolbar();
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVariables.getInstance().setUserToSee(CurrentUser.getInstance());

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).showLoader();
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                        navController.navigate(R.id.action_to_settingsFragment);
                    }
                }, 200);
            }
        });

        notificheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.getInstance().setUserToSee(CurrentUser.getInstance());
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                navController.navigate(R.id.action_to_UserPageFragment);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.getInstance().setUserToSee(CurrentUser.getInstance());
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                navController.navigate(R.id.action_to_homepageFragment);
            }
        });

        IndietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.getInstance().setUserToSee(CurrentUser.getInstance());
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.gestioneUtentiFragment) {
                    navController.navigate(R.id.settingsFragment);
                    MainActivity.setLogoVisible();
                    return;
                }

                navController.navigate(GlobalVariables.getInstance().getLastDestinationId());
                MainActivity.setLogoVisible();
            }
        });
    }

    public static void handleAggiornaToolbar() {
        try {
            char initial = CurrentUser.getInstance().getUsername().charAt(0);
            accountBtn.setText(String.valueOf(initial));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}