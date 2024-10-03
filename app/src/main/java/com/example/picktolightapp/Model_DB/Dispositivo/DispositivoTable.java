package com.example.picktolightapp.Model_DB.Dispositivo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;

import java.util.ArrayList;
import java.util.List;

public class DispositivoTable {

    public static final String TABLE_NAME = "dispositivi";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_QTY_MIN = "qty_min";
    public static final String COLUMN_ID_COMPONENTE = "id_componente";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_QTY + " INTEGER, " +
                    COLUMN_QTY_MIN + " INTEGER, " +
                    COLUMN_ID_COMPONENTE + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_ID_COMPONENTE + ") REFERENCES componenti(id) " +
                    "ON DELETE SET NULL ON UPDATE CASCADE)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_QTY + " INTEGER, " +
                COLUMN_QTY_MIN + " INTEGER, " +
                COLUMN_ID_COMPONENTE + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ID_COMPONENTE + ") REFERENCES componenti(id) " +
                "ON DELETE SET NULL ON UPDATE CASCADE)";
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(createTableIfNotExists);
    }

    public static void addDispositivo(Context context, Dispositivo dispositivo) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, dispositivo.getId());
        values.put(COLUMN_QTY, dispositivo.getQty());
        values.put(COLUMN_QTY_MIN, dispositivo.getQty_min());
        values.put(COLUMN_ID_COMPONENTE, dispositivo.getComponente().getId());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static List<Dispositivo> getAllDispositivi(Context context) {
        List<Dispositivo> dispositivi = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                int qty = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY));
                int qtyMin = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY_MIN));
                int idComponente = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_COMPONENTE));
                Dispositivo dispositivo = new Dispositivo(id, qty, qtyMin);
                dispositivo.setComponente(ComponenteTable.getComponenteById(context,idComponente));
                dispositivi.add(dispositivo);
                Log.d("DISPOSITIVOTABLE", "getAllDispositivi: " + dispositivo.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dispositivi;
    }

    public static boolean isDispositivoOnDB(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean dispositivoExists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return dispositivoExists;
    }

    public static boolean updateQtyById(Context context, int id, int newQty) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_QTY, newQty);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean updateQtyMinById(Context context, int id, int newQtyMin) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_QTY_MIN, newQtyMin);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean updateComponenteById(Context context, int id, int newIdComponente) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_COMPONENTE, newIdComponente);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean deleteDispositivo(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();

        return rowsDeleted > 0;
    }

    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public static Dispositivo getDispositivoById(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definisce la selezione e gli argomenti della selezione per la query
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Esegue la query per ottenere il dispositivo corrispondente all'ID
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        Dispositivo dispositivo = null;
        if (cursor.moveToFirst()) {
            int qty = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY));
            int qtyMin = cursor.getInt(cursor.getColumnIndex(COLUMN_QTY_MIN));
            int idComponente = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_COMPONENTE));

            // Crea il dispositivo
            dispositivo = new Dispositivo(id, qty, qtyMin);

            // Recupera il componente associato e lo assegna al dispositivo
            dispositivo.setComponente(ComponenteTable.getComponenteById(context, idComponente));

            Log.d("DISPOSITIVOTABLE", "getDispositivoById: " + dispositivo.toString());
        }

        cursor.close();
        db.close();

        return dispositivo;
    }

}
