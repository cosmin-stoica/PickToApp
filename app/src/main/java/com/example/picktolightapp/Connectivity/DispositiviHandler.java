package com.example.picktolightapp.Connectivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Live.LiveFragment;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DispositiviHandler {

    static Context context = null;
    static LayoutInflater layoutInflater;

    private static AlertDialog loadingDialog;
    static List<String> listDispositiviResponses = new ArrayList<>();

    private static final Handler messageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            Log.d("MyServer", "handleMessage: " + message);
            listDispositiviResponses.add(message);
        }
    };


    public static void getDispositiviRilevato(Context context, LayoutInflater layoutInflater, LiveFragment.OnDispositiviRilevatiListener listener) {
        DispositiviHandler.context = context;
        DispositiviHandler.layoutInflater = layoutInflater;

        for(Dispositivo dispositivo : GlobalVariables.getInstance().getCurrentDispositivi()) {
            if(!getDispositivibyServer(context,layoutInflater,dispositivo)){
                return;
            }
            else{
                DispositivoStoricoTable.addDispositivoStorico(context, new DispositivoStorico(dispositivo.getId(), "Dispositivo in fase di rilevazione"));
            }
        }
        showLoadingDialog();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkRilevatoStrings();
            hideLoadingDialog();
            if (listener != null) {
                listener.onDispositiviRilevati();
            }
        }, 1000);
    }



    public static boolean getDispositivibyServer(Context contextGiven,LayoutInflater layoutInflaterGiven, Dispositivo dispositivo) {

        String ip, port;
        int serverPort;
        try {
            ip = GlobalVariables.getInstance().getIP();
            port = GlobalVariables.getInstance().getPort();
            serverPort = Integer.parseInt(port);
        }catch (Exception e){
            DialogsHandler.showErrorDialog(contextGiven, layoutInflaterGiven, "Errore", "Ip e porta del server non configurati", null);
            return returnDispositivoCondition(false, dispositivo);
        }

        TcpClient tcpClient = new TcpClient(contextGiven, ip, serverPort, dispositivo.getId() + ":GS");
        if (contextGiven != null) {
            Server server = Server.getInstance(8001, contextGiven);
            server.setMessageHandler(messageHandler);
        }
        try {
            Future<SendMessageResult> futureResult = tcpClient.sendMessage();
            SendMessageResult result = futureResult.get(2, TimeUnit.SECONDS);

            if (result.isSuccess()) {
            } else {
                DialogsHandler.showErrorDialog(contextGiven, layoutInflaterGiven, "Errore", "Messaggio rifiutato: " + result.getErrorMessage(), null);
            }
            return returnDispositivoCondition(true, dispositivo);
        } catch (InterruptedException | ExecutionException e) {
            DialogsHandler.showErrorDialog(contextGiven, layoutInflaterGiven, "Errore", "Errore durante l'invio del messaggio: " + e.getMessage(), null);
            return returnDispositivoCondition(false, dispositivo);
        } catch (TimeoutException e) {
            DialogsHandler.showErrorDialog(contextGiven, layoutInflaterGiven, "Errore", "Errore durante l'invio del messaggio: TIMEOUT", null);
            return returnDispositivoCondition(false, dispositivo);
        }
    }


    private static boolean returnDispositivoCondition(boolean toRet, Dispositivo dispositivo){
        if(toRet) {
            DispositivoStoricoTable.addDispositivoStorico(context, new DispositivoStorico(dispositivo.getId(), "Dispositivo rilevato con successo"));
            return true;
        }
        else{
            DispositivoStoricoTable.addDispositivoStorico(context, new DispositivoStorico(dispositivo.getId(), "Dispositivo non rilevato, errore"));
            return false;
        }
    }

    // Method to show loading dialog
    private static void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = layoutInflater;
        View loadingView = inflater.inflate(R.layout.loading, null);
        builder.setView(loadingView);
        builder.setCancelable(false); // Prevent the user from dismissing the dialog
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    // Method to hide loading dialog
    private static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    static void checkRilevatoStrings(){


        for(Dispositivo a : GlobalVariables.getInstance().getCurrentDispositivi()){
            a.setRilevato(false);
        }

        List<Dispositivo> checked = new ArrayList<>();
        for (String toCheck : listDispositiviResponses){
            String[] components = toCheck.split(":");
            String id = components[0];
            Dispositivo d = findDispositivoByID(Integer.parseInt(id));
            if(d != null) {
                d.setRilevato(true);
                checked.add(d);
                Log.d("MyServer", "Dispositivo con id " + id + " è true");
            }
        }

        for(Dispositivo a : GlobalVariables.getInstance().getCurrentDispositivi()){
            Log.d("MyServer", "Dispositivo con id " + a.getId() + " è " + a.isRilevato());
        }

        listDispositiviResponses.clear();
    }

    static Dispositivo findDispositivoByID(int ID){
        for(Dispositivo dispositivoToCheck : GlobalVariables.getInstance().getCurrentDispositivi()){
            if(dispositivoToCheck.getId() == ID)
                return dispositivoToCheck;
        }
        return null;
    }

}
