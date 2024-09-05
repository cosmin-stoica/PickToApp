package com.example.picktolightapp.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.R;

import java.util.Arrays;

public class SettingsFragment extends Fragment {

    String[] settingsOptions;
    SettingsAdapter adapter;
    ListView listView;
    static ImageButton indietroBtn;
    static TextView headerSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings, container, false);


        settingsOptions = getResources().getStringArray(R.array.settings_options);
        String [] lastOptions = GlobalVariables.getInstance().getLastUserManagementOptions();

        adapter = new SettingsAdapter(requireContext(), lastOptions);
        headerSettings = view.findViewById(R.id.headerSettings);

        indietroBtn = view.findViewById(R.id.back_button);
        indietroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Torna indietro
            }
        });

        indietroBtn.setVisibility(View.INVISIBLE);

        listView = view.findViewById(R.id.settings_list);
        listView.setAdapter(adapter);

        TextView userName = view.findViewById(R.id.user_name);
        TextView userNameInitial = view.findViewById(R.id.profile_initial);
        CurrentUser currentUser = CurrentUser.getInstance();
        if(currentUser != null && currentUser.getUsername() != null) {
            String currentUserName = currentUser.getUsername();
            userName.setText(currentUserName);
            userNameInitial.setText(String.valueOf(currentUserName.charAt(0)));
        }

        if(!Arrays.equals(settingsOptions,lastOptions)){
            makeBtnVisibile();
        }

        switch (GlobalVariables.getInstance().getLastOptionGroupName()){
            case "Main":
                headerSettings.setText("Main");
                break;
            case "Gestione utenti":
                headerSettings.setText("Gestione utenti");
                break;
            case "Gestione dispositivi":
                headerSettings.setText("Gestione dispositivi");
                break;
            default:
                headerSettings.setText("Undefinied");
        }



        return view;
    }

    public static void makeBtnVisibile(){
        indietroBtn.setVisibility(View.VISIBLE);
    }

    public void onBackPressed(){

        String [] settingsOptions = getResources().getStringArray(R.array.settings_options);
        GlobalVariables.getInstance().setLastUserManagementOptions(settingsOptions);
        adapter = new SettingsAdapter(requireContext(), settingsOptions);
        listView.setAdapter(adapter);

        indietroBtn.setVisibility(View.INVISIBLE);
        GlobalVariables.getInstance().setLastOptionGroupName("Main");
        headerSettings.setText("Main");
    }

    public static void setHeaderText(String toSet){
        headerSettings.setText(toSet);
    }
}
