package com.example.picktolightapp.DispositiviHome;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.example.picktolightapp.Connectivity.DispositiviHandler;
import com.example.picktolightapp.Connectivity.SendMessageResult;
import com.example.picktolightapp.Connectivity.Server;
import com.example.picktolightapp.Connectivity.TcpClient;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DispositivoAdapter extends BaseAdapter {

    private Context context;
    private List<Dispositivo> dispositivoList;
    NavController navController;

    public DispositivoAdapter(Context context, NavController navController) {
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

        ordinaLista();
        Dispositivo dispositivo = dispositivoList.get(position);

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewQty = convertView.findViewById(R.id.textViewDescription);
        Button visualizeBtn = convertView.findViewById(R.id.visualizeDispo);
        Button rilevaBtn = convertView.findViewById(R.id.rilevaDispo);
        Button eliminaBtn = convertView.findViewById(R.id.eliminaDispo);


        if (dispositivo.isRilevato()) {
            textViewId.setBackgroundResource(R.drawable.rounded_squaregreen);
            textViewId.setTextColor(Color.WHITE);
        } else {
            textViewId.setBackgroundResource(R.drawable.rounded_square);
            textViewId.setTextColor(R.color.gray_dark);
        }

        eliminaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.DELETE_DISP,true)) {
                    return;
                }

                DialogsHandler.showConfirmDialog(
                        context,
                        LayoutInflater.from(context),
                        "Conferma",
                        "Sei sicuro di voler eliminare il dispositivo? L'azione è irreversibile",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               GlobalVariables.getInstance().getCurrentDispositivi().remove(dispositivo);
                               DispositivoTable.deleteDispositivo(context,dispositivo.getId());
                               dispositivoList = GlobalVariables.getInstance().getCurrentDispositivi();
                            }
                        },
                        null
                );

            }
        });


        rilevaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.DETECT_DISP,true)) {
                    return;
                }
                DispositiviHandler.getDispositivibyServer(context,LayoutInflater.from(context),dispositivo);
            }
        });

        visualizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.VIEW_DISP,true)) {
                    return;
                }

                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.getInstance().setDispositivoToSee(dispositivo);
                        GlobalVariables.getInstance().setLastDestinationId(R.id.DispositiviHomeFragment);
                        GlobalVariables.getInstance().setDispositivoNeedToCreate(false);
                        navController.navigate(R.id.action_DispositiviHomeFragment_to_DispositivoVisualizeFragment);
                    }
                }, 1000);
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


    private void ordinaLista() {
        if (dispositivoList != null) {
            dispositivoList.sort(Comparator.comparingInt(Dispositivo::getId));
        }
    }

}
