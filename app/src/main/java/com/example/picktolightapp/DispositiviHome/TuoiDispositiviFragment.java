package com.example.picktolightapp.DispositiviHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.picktolightapp.Connectivity.SendMessageResult;
import com.example.picktolightapp.Connectivity.TcpClient;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model.Dispositivo.DispositivoTable;
import com.example.picktolightapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TuoiDispositiviFragment extends Fragment {

    DispositivoAdapter dispositivoAdapter;
    ListView listView;
    private AlertDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuoi_dispositivi, container, false);

        Button rilevaDispositivi = view.findViewById(R.id.btnRilevaDispositivi);
        listView = view.findViewById(R.id.listView);

        List<Dispositivo> dispositivoList = new ArrayList<>();

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        dispositivoAdapter = new DispositivoAdapter(requireContext(),navController);
        listView.setAdapter(dispositivoAdapter);

        rilevaDispositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispositivoList.clear();

                //getDispositivibyServer();

                dispositivoList.addAll(DispositivoTable.getAllDispositivi(requireContext()));
                dispositivoList.add(new Dispositivo(0,0,0));
                dispositivoList.add(new Dispositivo(1,1,1));
                GlobalVariables.getInstance().setCurrentDispositivi(dispositivoList);
                dispositivoAdapter.updateData(dispositivoList);
            }
        });

        return view;
    }

    private void getDispositivibyServer() {
        String ip = GlobalVariables.getInstance().getIP();
        String port = GlobalVariables.getInstance().getPort();
        int serverPort = Integer.parseInt(port);

        TcpClient tcpClient = new TcpClient(requireContext(), ip, serverPort, "Status");

        try {
            Future<SendMessageResult> futureResult = tcpClient.sendMessage();
            SendMessageResult result = futureResult.get(2, TimeUnit.SECONDS);

            if (result.isSuccess()) {
                // Show loading dialog and wait for 1.5 seconds
                showLoadingDialog();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    DialogsHandler.showSuccessDialog(requireContext(), getLayoutInflater(), "Successo", "Dispositivi rilevati con successo", null);
                    hideLoadingDialog();
                }, 1500);
            } else {
                // Show error dialog immediately
                DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Messaggio rifiutato: " + result.getErrorMessage(), null);
            }
        } catch (InterruptedException | ExecutionException e) {
            // Show error dialog immediately
            DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Errore durante l'invio del messaggio: " + e.getMessage(), null);
        } catch (TimeoutException e) {
            // Show error dialog immediately
            DialogsHandler.showErrorDialog(requireContext(), getLayoutInflater(), "Errore", "Errore durante l'invio del messaggio: TIMEOUT", null);
        }
    }

    // Method to show loading dialog
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View loadingView = inflater.inflate(R.layout.loading, null);
        builder.setView(loadingView);
        builder.setCancelable(false); // Prevent the user from dismissing the dialog
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    // Method to hide loading dialog
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

}
