package com.example.picktolightapp.Settings.GestioneDispositivi.ConfigurazioneRete;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Global.GlobalTable;
import com.example.picktolightapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class ConfigurazioneReteFragment extends Fragment {

    TextInputEditText editTextPort, editTextIP;
    public ConfigurazioneReteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.configurazione_rete, container, false);

        MainActivity.setLogoInvisible();

        String oldIp = GlobalVariables.getInstance().getIP();
        String oldPort = GlobalVariables.getInstance().getPort();

        TextView currentIP = view.findViewById(R.id.currentIP);
        currentIP.setText(getIP(requireContext()));


        editTextIP = view.findViewById(R.id.editTextIP);
        editTextPort = view.findViewById(R.id.editTextPort);
        Button buttonChangeParams = view.findViewById(R.id.buttonChangeParams);

        if (!oldIp.isEmpty() && !oldPort.isEmpty()) {
            editTextIP.setText(oldIp);
            editTextPort.setText(oldPort);
        }
        buttonChangeParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeParams();
            }
        });

        return view;
    }

    private void changeParams() {
        String newIp = editTextIP.getText().toString();
        String newPort = editTextPort.getText().toString();

        GlobalVariables.getInstance().setIP(newIp);
        GlobalVariables.getInstance().setPort(newPort);

        if(GlobalTable.setIp(requireContext(),newIp) && GlobalTable.setPort(requireContext(),newPort)){
            DialogsHandler.showSuccessDialog(requireContext(), getLayoutInflater(), "Successo", "Parametri cambiati con successo", null);
        }
        else{
            DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Comunicazione con il database non riuscita", null);
        }
    }

    public String getIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            String ipString = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

            return ipString;
        }

        return "Indirizzo IP non disponibile";
    }

}
