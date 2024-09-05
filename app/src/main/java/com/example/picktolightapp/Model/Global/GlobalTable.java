package com.example.picktolightapp.Model.Global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;

public class GlobalTable {

    public static final String TABLE_NAME = "global";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VALUE = "value";

    private static final int ID_IP = 0;  // ID per l'indirizzo IP
    private static final int ID_PORT = 1;  // ID per la porta

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +  // Non AUTOINCREMENT
                    COLUMN_VALUE + " TEXT NOT NULL)";

    // Metodo per creare la tabella
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

        ContentValues ipValue = new ContentValues();
        ipValue.put(COLUMN_ID, ID_IP);
        ipValue.put(COLUMN_VALUE, ""); // IP di default vuoto
        db.insert(TABLE_NAME, null, ipValue);

        ContentValues portValue = new ContentValues();
        portValue.put(COLUMN_ID, ID_PORT);
        portValue.put(COLUMN_VALUE, ""); // Porta di default vuota
        db.insert(TABLE_NAME, null, portValue);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_VALUE + " TEXT NOT NULL)";
        db.execSQL(createTableIfNotExists);
    }


    // Metodo per ottenere l'IP
    public static String getIp(Context context) {
        return getValue(context, ID_IP);
    }

    // Metodo per impostare l'IP
    public static boolean setIp(Context context, String ip) {
        return setValue(context, ID_IP, ip);
    }

    // Metodo per ottenere la porta
    public static String getPort(Context context) {
        return getValue(context, ID_PORT);
    }

    // Metodo per impostare la porta
    public static boolean setPort(Context context, String port) {
        return setValue(context, ID_PORT, port);
    }

    // Metodo privato per recuperare il valore (IP o Porta) tramite ID
    private static String getValue(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String value = "";

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_VALUE}, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));
        }

        cursor.close();
        db.close();
        return value;
    }


    private static boolean setValue(Context context, int id, String newValue) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, newValue);

        // Aggiorna la riga corrispondente e ottieni il numero di righe interessate
        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        // Restituisce true se almeno una riga è stata aggiornata, altrimenti false
        return rowsAffected > 0;
    }
}
