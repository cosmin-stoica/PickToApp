package com.example.picktolightapp.Homepage;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.CAN.CANDriver;
import com.example.picktolightapp.COM.COMDriver;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;

public class HomepageFragment extends Fragment {

    //private CANDriver canDriver;
    //private COMDriver comDriver;
    private static final String TAG = "HomepageFragment";
    ImageButton utentiButton,dispositiviButton,componenteButton,liveButton,avvioButton,ricetteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);
        utentiButton = view.findViewById(R.id.utentiButton);
        dispositiviButton = view.findViewById(R.id.dispositiviButton);
        componenteButton = view.findViewById(R.id.componentiButton);
        liveButton = view.findViewById(R.id.liveButton);
        avvioButton = view.findViewById(R.id.avvioButton);
        ricetteButton = view.findViewById(R.id.ricetteButton);

        NavController navController = NavHostFragment.findNavController(this);

        utentiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_to_UserPageFragment);
                MainActivity.setLogoVisible();
            }
        });

        dispositiviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.VIEW_DISP,true)) {
                    return;
                }

                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_to_DispositiviHomeFragment);
                    }
                }, 1000);
            }
        });

        componenteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.VIEW_COMP,true)) {
                    return;
                }

                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_to_ComponenteHomeFragment);
                    }
                }, 600);
            }
        });


        liveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.VIEW_LIVE,true)) {
                    return;
                }

                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_to_LiveFragment);
                    }
                }, 600);
            }
        });

        avvioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.START_WORK,true)) {
                    return;
                }

                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_HomepageFragment_to_LavorazioneFragment);
                    }
                }, 400);
            }
        });

        ricetteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.START_WORK,true)) {
                    return;
                }
            }
        });

        /*Button provaCanButton = view.findViewById(R.id.ProvaCanButton);
        Button provaComButton = view.findViewById(R.id.ProvaCOMButton);
        canDriver = new CANDriver(requireContext());
        comDriver = new COMDriver(requireContext());*/

        /*provaCanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProvaCanButtonClick();
            }
        });
        provaComButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProvaComButtonClick();
            }
        });*/

        return view;
    }

    /*private void onProvaComButtonClick() {
        comDriver.openDevice(getContext());
        comDriver.sendString("provaAPP");
    }

    /*private void onProvaCanButtonClick() {
        if (!canDriver.openConnection()) {
            Log.w(TAG, "Failed to open connection to CAN device.");
            return;
        }

        // Step 3: Initialize CAN
        if (!canDriver.initCAN()) {
            Log.w(TAG, "Failed to initialize CAN.");
            return;
        }

        // Step 4: Set CAN filter
        if (!canDriver.setFilter()) {
            Log.w(TAG, "Failed to set CAN filter.");
            return;
        }

        // Step 5: Start CAN communication
        if (!canDriver.startCAN()) {
            Log.w(TAG, "Failed to start CAN communication.");
            return;
        }

        // Step 6: Send data
        if (!canDriver.sendData()) {
            Log.w(TAG, "Failed to send data over CAN.");
        }

        // Step 7: Receive data
        //canDriver.receiveData();

        // Step 8: Close the connection
        canDriver.closeConnection();
    }*/
}
