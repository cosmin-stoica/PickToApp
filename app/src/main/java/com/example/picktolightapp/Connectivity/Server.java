package com.example.picktolightapp.Connectivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.Event.EventWriter;
import com.example.picktolightapp.Model_DB.Event.TipoEvento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final String TAG = "MyServer";
    private static Server instance;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    private Thread serverThread;
    private EventWriter eventWriter;
    private Context context;
    private Handler messageHandler;

    private Server(int port, Context context) {
        this.port = port;
        this.context = context;
        this.eventWriter = EventWriter.getInstance(context);
    }

    public static synchronized Server getInstance(int port, Context context) {
        if (instance == null) {
            instance = new Server(port, context);
        }
        return instance;
    }

    // Metodo per impostare l'Handler dal Fragment
    public void setMessageHandler(Handler handler) {
        this.messageHandler = handler;
        Log.d(TAG,"Message handler setted");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startServer() {
        isRunning = true;

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                Log.d(TAG, "Server avviato sulla porta: " + port);
                eventWriter.logEvent(TipoEvento.SERVER, "Server avviato sulla porta: " + port);

                while (isRunning) {
                    eventWriter.logEvent(TipoEvento.SERVER, "In attesa di connessioni...");
                    Socket clientSocket = serverSocket.accept();

                    new ClientHandler(clientSocket).start();
                }
            } catch (IOException e) {
                Log.e(TAG, "Errore nel server: " + e.getMessage());
                eventWriter.logEvent(TipoEvento.ERROR, "Errore nel server: " + e.getMessage());
            } finally {
                stopServer();
            }
        });
        serverThread.start();
    }

    public void stopServer() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                Log.d(TAG, "Server fermato.");
                eventWriter.logEvent(TipoEvento.SERVER, "Server fermato.");
            } catch (IOException e) {
                eventWriter.logEvent(TipoEvento.ERROR, "Errore durante la chiusura del server: " + e.getMessage());
                Log.e(TAG, "Errore durante la chiusura del server: " + e.getMessage());
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    eventWriter.logEvent(TipoEvento.SERVER, "Messaggio ricevuto dal client: " + clientMessage);
                    Log.d(TAG, "Messaggio ricevuto dal client: " + clientMessage);

                    if (messageHandler != null) {
                        Message msg = Message.obtain();
                        msg.obj = clientMessage;
                        messageHandler.sendMessage(msg);
                    }

                    if (clientMessage.startsWith("Status:"))
                        handleStatusMessage(clientMessage);
                }
            } catch (IOException e) {
                Log.e(TAG, "Errore nella comunicazione con il client: " + e.getMessage());
                eventWriter.logEvent(TipoEvento.ERROR, "Errore nella comunicazione con il client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    eventWriter.logEvent(TipoEvento.ERROR, "Errore durante la chiusura del client socket: " + e.getMessage());
                    Log.e(TAG, "Errore durante la chiusura del client socket: " + e.getMessage());
                }
            }
        }

        private void handleStatusMessage(String message) {
            eventWriter.logEvent(TipoEvento.DEBUG, "Arrivato messaggio di stato: " + message);
            List<Integer> listID = StringModulator.getIntegerListFromString(message);
            eventWriter.logEvent(TipoEvento.DEBUG, "Lista di dispositivi: " + listID);
            List<Dispositivo> dispositivoList = new ArrayList<>();
            for (int id : listID) {
                dispositivoList.add(new Dispositivo(id, 0, 0));
            }

            DispositivoTable.clearAll(context);
            for (Dispositivo dispositivo : dispositivoList) {
                DispositivoTable.addDispositivo(context, dispositivo);
            }

            GlobalVariables.getInstance().setCurrentDispositivi(dispositivoList);
        }
    }
}
