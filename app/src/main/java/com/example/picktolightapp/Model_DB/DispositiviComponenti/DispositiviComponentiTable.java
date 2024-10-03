package com.example.picktolightapp.Model_DB.DispositiviComponenti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.picktolightapp.DatabaseHelper;
import com.example.picktolightapp.Model_DB.Componente.Componente;
import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;

import java.util.ArrayList;
import java.util.List;

public class DispositiviComponentiTable {

    public static final String TABLE_NAME = "dispositivi_componenti";
    public static final String COLUMN_ID_DISPOSITIVO = "id_dispositivo";
    public static final String COLUMN_ID_COMPONENTE = "id_componente";

    // SQL statement to create the dispositivi_componenti table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID_DISPOSITIVO + " INTEGER, " +
                    COLUMN_ID_COMPONENTE + " INTEGER, " +
                    "PRIMARY KEY (" + COLUMN_ID_DISPOSITIVO + ", " + COLUMN_ID_COMPONENTE + "), " +
                    "FOREIGN KEY (" + COLUMN_ID_DISPOSITIVO + ") REFERENCES dispositivi(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + COLUMN_ID_COMPONENTE + ") REFERENCES componenti(id) ON DELETE CASCADE)";

    // Create the table
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade the table
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID_DISPOSITIVO + " INTEGER, " +
                COLUMN_ID_COMPONENTE + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_ID_DISPOSITIVO + ", " + COLUMN_ID_COMPONENTE + "), " +
                "FOREIGN KEY (" + COLUMN_ID_DISPOSITIVO + ") REFERENCES dispositivi(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COLUMN_ID_COMPONENTE + ") REFERENCES componenti(id) ON DELETE CASCADE)";
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(createTableIfNotExists);
    }

    // Add a new relation between dispositivo and componente
    public static void addDispositivoComponente(Context context, Dispositivo dispositivo, Componente componente) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DISPOSITIVO, dispositivo.getId());
        values.put(COLUMN_ID_COMPONENTE, componente.getId());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve all componenti associated with a specific dispositivo
    public static List<Componente> getComponentiForDispositivo(Context context, int dispositivoId) {
        List<Componente> componenti = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_ID_DISPOSITIVO + " = ?";
        String[] selectionArgs = {String.valueOf(dispositivoId)};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID_COMPONENTE}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int componenteId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_COMPONENTE));
                // You would need to fetch the Componente object using the ID here
                Componente componente = fetchComponenteById(context, componenteId);
                if (componente != null) {
                    componenti.add(componente);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return componenti;
    }

    // Retrieve all dispositivi associated with a specific componente
    public static List<Dispositivo> getDispositiviForComponente(Context context, int componenteId) {
        List<Dispositivo> dispositivi = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_ID_COMPONENTE + " = ?";
        String[] selectionArgs = {String.valueOf(componenteId)};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID_DISPOSITIVO}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int dispositivoId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_DISPOSITIVO));
                // You would need to fetch the Dispositivo object using the ID here
                Dispositivo dispositivo = fetchDispositivoById(context, dispositivoId);
                if (dispositivo != null) {
                    dispositivi.add(dispositivo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dispositivi;
    }

    // Delete a specific dispositivo-componente association
    public static boolean deleteDispositivoComponente(Context context, int dispositivoId, int componenteId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = COLUMN_ID_DISPOSITIVO + " = ? AND " + COLUMN_ID_COMPONENTE + " = ?";
        String[] selectionArgs = {String.valueOf(dispositivoId), String.valueOf(componenteId)};

        int rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();

        return rowsDeleted > 0;
    }

    // Clear all dispositivo-componente associations
    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    private static int fetchComponenteByDispositivo(Context context, Dispositivo dispositivo) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definisce la selezione e gli argomenti della selezione per la query
        String selection = "id_dispositivo = ?";
        String[] selectionArgs = {String.valueOf(dispositivo.getId())};

        // Esegue la query per ottenere l'id_componente associato all'id_dispositivo
        Cursor cursor = db.query("dispositivi_componenti", new String[]{"id_componente"}, selection, selectionArgs, null, null, null);

        int componenteId = -1;  // Valore di default se nessun componente è trovato
        if (cursor.moveToFirst()) {
            componenteId = cursor.getInt(cursor.getColumnIndex("id_componente"));
        }
        cursor.close();
        db.close();

        return componenteId;
    }

    private static Componente fetchComponenteById(Context context, int componenteId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(componenteId)};

        Cursor cursor = db.query("componenti", null, selection, selectionArgs, null, null, null);

        Componente componente = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String img = cursor.getString(cursor.getColumnIndex("img"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String barcode = cursor.getString(cursor.getColumnIndex("barcode"));

            componente = new Componente(id, img, name, barcode);
        }

        cursor.close();
        db.close();

        return componente;
    }


    private static Dispositivo fetchDispositivoById(Context context, int dispositivoId) {
        return null;
    }
}
