package com.example.picktolightapp.Model.Event;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventoTable {

    public static final String TABLE_NAME = "eventi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DESCRIZIONE = "descrizione";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIPO + " INTEGER, " +
                    COLUMN_TIMESTAMP + " TEXT, " +
                    COLUMN_DESCRIZIONE + " TEXT )";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIPO + " INTEGER, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_DESCRIZIONE + " TEXT)";
        db.execSQL(createTableIfNotExists);
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public static void addEvento(Context context, TipoEvento tipo, Timestamp timestamp, String descrizione) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIPO, tipo.getCodice());
        values.put(COLUMN_TIMESTAMP, timestamp.toString());
        values.put(COLUMN_DESCRIZIONE, descrizione);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static List<Evento> getEventsByTimestamp(Context context, Timestamp timestamp) {
        List<Evento> eventi = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definire l'intervallo di date: dal timestamp alle 00:00 fino a prima della mezzanotte successiva
        String startDate = timestamp.toString().substring(0, 10) + " 00:00:00";
        String endDate = timestamp.toString().substring(0, 10) + " 23:59:59.999";

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP + " BETWEEN ? AND ?";
        String[] selectionArgs = new String[]{startDate, endDate};

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                int tipo = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIPO));
                String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String descrizione = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIZIONE));

                TipoEvento tipoEvento = TipoEvento.fromCodice(tipo);
                Timestamp ts = Timestamp.valueOf(timestampStr);
                Evento evento = new Evento(tipoEvento, ts, descrizione);
                eventi.add(evento);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return eventi;
    }

    public static boolean deleteEventsByTimestamp(Context context, Timestamp timestamp) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String startDate = timestamp.toString().substring(0, 10) + " 00:00:00";
        String endDate = timestamp.toString().substring(0, 10) + " 23:59:59.999";

        int rowsDeleted = db.delete(
                EventoTable.TABLE_NAME,
                EventoTable.COLUMN_TIMESTAMP + " BETWEEN ? AND ?",
                new String[]{startDate, endDate}
        );

        //Log.d("Database", "Deleted " + rowsDeleted + " rows from the table.");
        db.close();
        return rowsDeleted > 0;
    }


    public static List<Evento> getAllEvents(Context context) {
        List<Evento> eventi = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int tipoCodice = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPO));
                TipoEvento tipo = TipoEvento.fromCodice(tipoCodice);
                @SuppressLint("Range") String timestampStr = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                Timestamp timestamp = Timestamp.valueOf(timestampStr);
                @SuppressLint("Range") String descrizione = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIZIONE));

                Evento evento = new Evento(tipo, timestamp, descrizione);
                eventi.add(evento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventi;
    }

    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


}