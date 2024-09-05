package com.example.picktolightapp.DispositiviHome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model.Dispositivo.Dispositivo;
import com.example.picktolightapp.R;

import java.util.ArrayList;
import java.util.List;

public class DispositivoAdapter extends BaseAdapter {

    private Context context;
    private List<Dispositivo> dispositivoList;
    NavController navController;

    public DispositivoAdapter(Context context,NavController navController) {
        this.context = context;
        this.navController = navController;
        this.dispositivoList = GlobalVariables.getInstance().getCurrentDispositivi();
    }

    @Override
    public int getCount() {
        return dispositivoList.size();
    }

    @Override
    public Object getItem(int position) {
        return dispositivoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dispositivo_item, parent, false);
        }

        Dispositivo dispositivo = dispositivoList.get(position);

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewQty = convertView.findViewById(R.id.textViewDescription);
        Button visualizeBtn = convertView.findViewById(R.id.visualizeDispo);

        visualizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_DispositiviHomeFragment_to_DispositivoVisualizeFragment);
            }
        });

        textViewId.setText(String.valueOf(dispositivo.getId()));
        textViewQty.setText("Dispositivo " + String.valueOf(dispositivo.getId()));
        return convertView;
    }

    public void updateData(List<Dispositivo> newDispositivoList) {
        this.dispositivoList = newDispositivoList;
        notifyDataSetChanged();
    }
}
