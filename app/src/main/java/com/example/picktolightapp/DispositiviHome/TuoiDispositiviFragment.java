package com.example.picktolightapp.DispositiviHome;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.picktolightapp.Connectivity.DispositiviHandler;
import com.example.picktolightapp.Connectivity.SendMessageResult;
import com.example.picktolightapp.Connectivity.Server;
import com.example.picktolightapp.Connectivity.TcpClient;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuoi_dispositivi, container, false);

        Button rilevaDispositivi = view.findViewById(R.id.btnRilevaDispositivi);
        listView = view.findViewById(R.id.listView);


        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        dispositivoAdapter = new DispositivoAdapter(requireContext(),navController);
        listView.setAdapter(dispositivoAdapter);

        rilevaDispositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DETECT_DISP,true)) {
                    return;
                }

                DispositiviHandler.getDispositiviRilevato(requireContext(),getLayoutInflater(),() -> {
                    dispositivoAdapter.updateData(GlobalVariables.getInstance().getCurrentDispositivi());
                    dispositivoAdapter.notifyDataSetChanged();
                });
            }
        });

        return view;
    }
}
