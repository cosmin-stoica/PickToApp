package com.example.picktolightapp.Model.Event;
import android.content.Context;

import java.sql.Timestamp;

public class EventWriter {

    private static EventWriter instance;
    private Context context;

    public EventWriter(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized EventWriter getInstance(Context context) {
        if (instance == null) {
            instance = new EventWriter(context);
        }
        return instance;
    }

    public void logEvent(TipoEvento tipo, String descrizione) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        EventoTable.addEvento(context, tipo, timestamp, descrizione);
    }

    public void clearAll(){
        EventoTable.clearAll(context);
    }
}

