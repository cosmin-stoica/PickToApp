package com.example.picktolightapp.Model_DB.DispositivoStorico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.picktolightapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class DispositivoStoricoTable {

    private static final String TABLE_NAME = "dispositivo_history";
    private static final String COLUMN_ID_DISPOSITIVO = "id_dispositivo";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID_DISPOSITIVO + " INTEGER, " +
                    COLUMN_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_ID_DISPOSITIVO + ") REFERENCES dispositivi(id) ON DELETE CASCADE)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID_DISPOSITIVO + " INTEGER, " +
                COLUMN_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_ID_DISPOSITIVO + ") REFERENCES dispositivi(id) ON DELETE CASCADE)";
        db.execSQL(createTableIfNotExists);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static void addDispositivoStorico(Context context, DispositivoStorico dispositivoStorico) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID_DISPOSITIVO, dispositivoStorico.getIdentifierDipositivo());
        values.put(COLUMN_DESCRIPTION, dispositivoStorico.getOperation());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static List<DispositivoStorico> getAllStorici(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<DispositivoStorico> toRet = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idDispositivo = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_DISPOSITIVO));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                toRet.add(new DispositivoStorico(idDispositivo,description,timestamp));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return toRet;
    }

    public static List<DispositivoStorico> getAllStoriciById(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<DispositivoStorico> toRet = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_DISPOSITIVO + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                int idDispositivo = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_DISPOSITIVO));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                toRet.add(new DispositivoStorico(idDispositivo,description,timestamp));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return toRet;
    }

    public static void clearAllByID(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID_DISPOSITIVO + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
