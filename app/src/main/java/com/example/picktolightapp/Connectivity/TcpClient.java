package com.example.picktolightapp.Connectivity;

import android.content.Context;

import com.example.picktolightapp.Model.Event.EventWriter;
import com.example.picktolightapp.Model.Event.TipoEvento;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TcpClient {

    private String serverIp;
    private int serverPort;
    private String messageToSend;
    private ExecutorService executorService;
    private Context context;
    EventWriter eventWriter;

    public TcpClient(Context context, String serverIp, int serverPort, String message) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageToSend = message;
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = context;
        eventWriter = EventWriter.getInstance(context);
    }

    public Future<SendMessageResult> sendMessage() {
        return executorService.submit(new Callable<SendMessageResult>() {
            @Override
            public SendMessageResult call() {
                SendMessageResult result = new SendMessageResult(false, "Unknown error");
                try {
                    Socket socket = new Socket(serverIp, serverPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(messageToSend);
                    eventWriter.logEvent(TipoEvento.DEBUG, "Inviato messaggio via TCP/IP: M:" + messageToSend + ", IP:" + serverIp + ", PORTA:" + serverPort);
                    out.close();
                    socket.close();
                    result.setSuccess(true);
                } catch (IOException e) {
                    eventWriter.logEvent(TipoEvento.ERROR, "Errore nell'invio di dati TCP/IP");
                    e.printStackTrace();
                    result.setSuccess(false);
                    result.setErrorMessage(e.getMessage());
                }
                return result;
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
