package com.example.picktolightapp.DispositiviHome;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.picktolightapp.Model.Dispositivo.DispositivoTable;
import com.example.picktolightapp.R;


public class DispositivoVisualizeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivo_visualize, container, false);

        return view;
    }


}
