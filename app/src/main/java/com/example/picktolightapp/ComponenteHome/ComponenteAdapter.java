package com.example.picktolightapp.ComponenteHome;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;

import java.util.Comparator;
import java.util.List;

public class ComponenteAdapter extends BaseAdapter {

    private Context context;
    private List<Componente> componenteList;
    NavController navController;
    private AlertDialog loadingDialog;
    TextView textViewId;
    String toRet = "";


    public ComponenteAdapter(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
        this.componenteList = ComponenteTable.getAllComponenti(context);
    }

    @Override
    public int getCount() {
        return componenteList.size();
    }

    @Override
    public Object getItem(int position) {
        return componenteList.get(position);
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
        Componente componente = componenteList.get(position);

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewQty = convertView.findViewById(R.id.textViewDescription);
        Button visualizeBtn = convertView.findViewById(R.id.visualizeDispo);
        Button rilevaBtn = convertView.findViewById(R.id.rilevaDispo);
        Button eliminaBtn = convertView.findViewById(R.id.eliminaDispo);


        /*if (componente.isRilevato()) {
            textViewId.setBackgroundResource(R.drawable.rounded_squaregreen);
            textViewId.setTextColor(Color.WHITE);
        } else {
            textViewId.setBackgroundResource(R.drawable.rounded_square);
            textViewId.setTextColor(R.color.gray_dark);
        }*/

        eliminaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!PermissionOperationsTable.userHasPermission(context, CurrentUser.getInstance(), Operation.DELETE_COMPONENT,true)) {
                    return;
                }

                DialogsHandler.showConfirmDialog(
                        context,
                        LayoutInflater.from(context),
                        "Conferma",
                        "Sei sicuro di voler eliminare il componente? L'azione è irreversibile",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                componenteList.remove(componente);
                                ComponenteTable.deleteComponente(context,componente.getId());
                            }
                        },
                        null
                );

            }
        });

        rilevaBtn.setVisibility(View.GONE);

        visualizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showLoader();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.getInstance().setComponenteToSee(componente);
                        GlobalVariables.getInstance().setLastDestinationId(R.id.ComponenteHomeFragment);
                        GlobalVariables.getInstance().setComponenteNeedToCreate(false);
                        navController.navigate(R.id.action_ComponenteHomeFragment_to_CreaComponenteFragment);
                    }
                }, 1000);
            }
        });

        textViewId.setText(String.valueOf(componente.getId()));
        textViewQty.setText(componente.getName());
        return convertView;
    }

    public void updateData(List<Componente> newComponenteList) {
        componenteList = newComponenteList;
        notifyDataSetChanged();
    }

    // Method to show loading dialog
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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


    private void ordinaLista() {
        if (componenteList != null) {
            componenteList.sort(Comparator.comparingInt(Componente::getId));
        }
    }

}
