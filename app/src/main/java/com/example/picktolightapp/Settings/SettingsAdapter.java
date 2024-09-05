package com.example.picktolightapp.Settings;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model.Operation.Operation;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.CurrentUser;
import com.example.picktolightapp.R;

public class SettingsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    ImageButton indietroBtn;
    TextView headerSettings;

    public SettingsAdapter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        View generalView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.list_item, parent, false);
            generalView = inflater.inflate(R.layout.settings, parent, false);
        }

        TextView textView = rowView.findViewById(R.id.text_view_item);
        ImageButton iconButton = rowView.findViewById(R.id.icon_button);
        indietroBtn = rowView.findViewById(R.id.back_button);
        headerSettings = generalView.findViewById(R.id.headerSettings);

        textView.setText(values[position]);

        View finalRowView = rowView;
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleItemClick(finalRowView, values[position], v);
            }
        };

        textView.setOnClickListener(clickListener);
        iconButton.setOnClickListener(clickListener);

        return rowView;
    }

    private void handleItemClick(View rowView, String value, View view) {
        int colorFrom = context.getResources().getColor(R.color.settings_normal_background);
        int colorTo = context.getResources().getColor(R.color.settings_pressed_background);

        ObjectAnimator animator = ObjectAnimator.ofObject(rowView, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
        animator.setDuration(200);
        animator.start();

        handleSettingsOptions(value, view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator reverseAnimator = ObjectAnimator.ofObject(rowView, "backgroundColor", new ArgbEvaluator(), colorTo, colorFrom);
                reverseAnimator.setDuration(200);
                reverseAnimator.start();
            }
        }, 400);
    }

    private void loadUserManagementOptions() {
        String[] userManagementOptions = context.getResources().getStringArray(R.array.utenti_settings_options);
        GlobalVariables.getInstance().setLastUserManagementOptions(userManagementOptions);
        SettingsAdapter newAdapter = new SettingsAdapter(context, userManagementOptions);

        // Assumi che il ListView o RecyclerView che stai usando si chiami listView
        ListView listView = ((Activity) context).findViewById(R.id.settings_list); // Modifica secondo il tuo layout
        listView.setAdapter(newAdapter);

        SettingsFragment.setHeaderText("Gestione utenti");
    }

    private void loadDispositiviManagementOptions() {
        String[] userManagementOptions = context.getResources().getStringArray(R.array.dispositivi_settings_options);
        GlobalVariables.getInstance().setLastUserManagementOptions(userManagementOptions);
        SettingsAdapter newAdapter = new SettingsAdapter(context, userManagementOptions);

        ListView listView = ((Activity) context).findViewById(R.id.settings_list);
        listView.setAdapter(newAdapter);

        SettingsFragment.setHeaderText("Gestione dispositivi");
    }

    private void handleSettingsOptions(String option, View view){
    NavController navController = Navigation.findNavController(view);
    GlobalVariables.getInstance().setLastDestinationId(R.id.settingsFragment);
        switch (option) {
            case "Visualizza Log":

                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.VIEW_LOG,true)){
                    return;
                }

                navController.navigate(R.id.action_settingsFragment_to_logFragment);
                break;
            case "Gestione utenti":
                loadUserManagementOptions();
                SettingsFragment.makeBtnVisibile();
                GlobalVariables.getInstance().setLastOptionGroupName("Gestione utenti");
                break;
            case "Visualizza utenti":

                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.VIEW_USERS,true)) {
                    return;
                }
                navController.navigate(R.id.action_settingsFragment_to_gestioneUtentiFragment);
                break;
            case "Crea utente":
                navController.navigate(R.id.action_settingsFragment_to_creaUtenteFragment);
                break;
            case "Gestione dispositivi":
                loadDispositiviManagementOptions();
                SettingsFragment.makeBtnVisibile();
                GlobalVariables.getInstance().setLastOptionGroupName("Gestione dispositivi");
                break;
            case "Test TCP/IP":
                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.TEST_TCPIP,true)) {
                    return;
                }
                navController.navigate(R.id.action_settingsFragment_to_TCPIPFragment);
                break;
            case "Configurazione":
                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.CONF_NET,true)) {
                    return;
                }
                navController.navigate(R.id.action_settingsFragment_to_ConfigurazioneReteFragment);
                break;
            default:
                Toast.makeText(context, "Opzione non riconosciuta", Toast.LENGTH_SHORT).show();
        }
    }
}
