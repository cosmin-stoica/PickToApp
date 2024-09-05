package com.example.picktolightapp.Settings.GestioneDispositivi.TestTCPIP;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.picktolightapp.Connectivity.SendMessageResult;
import com.example.picktolightapp.Connectivity.Server;
import com.example.picktolightapp.Connectivity.TcpClient;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model.Event.EventWriter;
import com.example.picktolightapp.Model.Event.TipoEvento;
import com.example.picktolightapp.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestTCPIPFragment extends Fragment {

    public TestTCPIPFragment() {
    }

    Button buttonSend,btnButtonSX,btnButtonDX;
    EditText editTextIp, editTextPort, editTextMessage;
    TextView textViewRicezione,dispositivoID;
    EditText editTextId;
    LinearLayout fakeDispositivo;
    CircleView circleView;

    private boolean isLooping = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final Handler messageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            textViewRicezione.setText(message);
            handleDispositivoMessage(message);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tcpip, container, false);

        MainActivity.setLogoInvisible();

        textViewRicezione = view.findViewById(R.id.textViewRicezione);
        fakeDispositivo = view.findViewById(R.id.fakeDispositivo);
        btnButtonSX = view.findViewById(R.id.btnButtonSX);
        btnButtonDX = view.findViewById(R.id.btnButtonDX);
        circleView = view.findViewById(R.id.circleView);
        dispositivoID = view.findViewById(R.id.dispositivoID);

        Button buttonSend = view.findViewById(R.id.btnSendClientTCP);
        Button buttonStatus = view.findViewById(R.id.btnStatus);
        Button buttonAbilita = view.findViewById(R.id.btnAbilita);
        Button buttonDisabilita = view.findViewById(R.id.btnDisabilita);
        Button buttonAccendiLedSX = view.findViewById(R.id.btnAccendiLedSX);
        Button buttonSpegniLedSX = view.findViewById(R.id.btnSpegniLedSX);
        Button buttonAccendiLedDX = view.findViewById(R.id.btnAccendiLedDX);
        Button buttonSpegniLedDX = view.findViewById(R.id.btnSpegniLedDX);
        Button buttonStatusLoop = view.findViewById(R.id.btnStatusLoop);



        editTextIp = view.findViewById(R.id.editTextIp);
        editTextPort = view.findViewById(R.id.editTextPort);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        editTextId = view.findViewById(R.id.editTextId);

        editTextIp.setText(GlobalVariables.getInstance().getIP());
        editTextPort.setText(GlobalVariables.getInstance().getPort());

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                tcpSendMessage(message,true);
            }
        });

        Context context = getContext();
        if (context != null) {
            Server server = Server.getInstance(8001, context);
            server.setMessageHandler(messageHandler);
        }
        buttonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("GS");
            }
        });

        buttonAbilita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("E1");
            }
        });

        buttonDisabilita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("E0");
            }
        });

        buttonAccendiLedSX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("LL1");
            }
        });

        buttonSpegniLedSX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("LL0");
            }
        });

        buttonAccendiLedDX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("LR1");
            }
        });

        buttonSpegniLedDX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageWithId("LR0");
            }
        });




        buttonStatusLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLooping) {
                    isLooping = true;
                    startStatusLoop();
                } else {
                    isLooping = false;
                    handler.removeCallbacks(runnable);
                }
            }
        });

        return view;
    }

    private void startStatusLoop() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isLooping) {
                    sendMessageWithId("GS");
                    handler.postDelayed(this, 200);
                }
            }
        };

        handler.post(runnable);
    }

    private void sendMessageWithId(String command) {
        String id = editTextId.getText().toString();
        String message = id + ":" + command;
        tcpSendMessage(message,false);
    }

    void tcpSendMessage(String message, boolean wantSuccess){

        String serverIp = editTextIp.getText().toString();
        String sServeport = editTextPort.getText().toString();

        if(serverIp.isEmpty()){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Indirizzo ip non inserito",
                    null
            );
            return;
        }
        if(sServeport.isEmpty()){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Porta non inserita",
                    null
            );
            return;
        }

        if(message.isEmpty()){
            DialogsHandler.showWarningDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Warning",
                    "Messaggio non inserito",
                    null
            );
            return;
        }

        int serverPort = Integer.parseInt(sServeport);
        TcpClient tcpClient = new TcpClient(requireContext(),serverIp, serverPort, message);

        try {
            Future<SendMessageResult> futureResult = tcpClient.sendMessage();
            SendMessageResult result = futureResult.get(2, TimeUnit.SECONDS);

            if(wantSuccess) {
                if (result.isSuccess()) {
                    DialogsHandler.showSuccessDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Successo",
                            "Messaggio inviato con successo!",
                            null
                    );
                } else {
                    DialogsHandler.showErrorDialog(
                            requireContext(),
                            getLayoutInflater(),
                            "Errore",
                            "Messaggio rifiutato: " + result.getErrorMessage(),
                            null
                    );
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            DialogsHandler.showErrorDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Errore",
                    "Errore durante l'invio del messaggio: " + e.getMessage(),
                    null
            );
        } catch (TimeoutException e) {
            DialogsHandler.showErrorDialog(
                    requireContext(),
                    getLayoutInflater(),
                    "Errore",
                    "Errore durante l'invio del messaggio: TIMEOUT",
                    null
            );
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        isLooping = false;
        handler.removeCallbacks(runnable);
    }

    private void handleDispositivoMessage(String message) {
        String[] components = message.split(":");
        String id = components[0];
        dispositivoID.setText(id);
        EventWriter eventWriter = EventWriter.getInstance(requireContext());
        for (int i = 1; i < components.length; i++) {
            String component = components[i];

            if (component.length() == 2) {
                // Caso per componenti con 2 caratteri (come "E0", "E1", "R0", "R1")
                switch (component) {
                    case "E0":
                        fakeDispositivo.setBackgroundResource(R.drawable.fakedispositivonormal);
                        break;
                    case "E1":
                        fakeDispositivo.setBackgroundResource(R.drawable.fakedispositivoattivo);
                        break;
                    case "R0":
                        break;
                    case "R1":
                        fakeDispositivo.setBackgroundResource(R.drawable.fakedispositivomano);
                        break;
                    default:
                        eventWriter.logEvent(TipoEvento.ERROR, "Arrivato Componente da Dispositivo sconosciuto: " + component);
                        break;
                }
            } else if (component.length() == 3) {
                // Caso per componenti con 3 caratteri (come "BL0", "BR1", "LL0", "LR1")
                switch (component) {
                    case "BL0":
                        clearButtonPressed(btnButtonSX);
                        break;
                    case "BL1":
                        makeButtonPressed(btnButtonSX);
                        break;
                    case "BR0":
                        clearButtonPressed(btnButtonDX);
                        break;
                    case "BR1":
                        makeButtonPressed(btnButtonDX);
                        break;
                    case "LL0":
                        circleView.setLXColor(Color.rgb(80, 0, 0));
                        break;
                    case "LL1":
                        circleView.setLXColor(Color.rgb(200, 0, 0));
                        break;
                    case "LR0":
                        circleView.setDXColor(Color.rgb(0, 80, 0));
                        break;
                    case "LR1":
                        circleView.setDXColor(Color.rgb(0, 200, 0));
                        break;
                    default:
                        eventWriter.logEvent(TipoEvento.ERROR, "Arrivato Componente da Dispositivo sconosciuto: " + component);
                        break;
                }
            } else {
                eventWriter.logEvent(TipoEvento.ERROR, "Arrivato Componente da Dispositivo sconosciuto: " + component);
            }
        }
    }

    void makeButtonPressed(Button button){
        button.setText("Pressed");
        button.setTextColor(Color.GREEN);
    }

    void clearButtonPressed(Button button){
        if(button == btnButtonSX)
            button.setText("Button SX");
        else
            button.setText("Button DX");

        button.setTextColor(getResources().getColor(R.color.indietro_color));
    }

}