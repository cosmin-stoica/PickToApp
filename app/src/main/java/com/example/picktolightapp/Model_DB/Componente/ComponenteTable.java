package com.example.picktolightapp.Model_DB.Componente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ComponenteTable {

    public static final String TABLE_NAME = "componenti";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMG = "img";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BARCODE = "barcode";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_IMG + " TEXT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_BARCODE + " TEXT)";

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
                COLUMN_IMG + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_BARCODE + " TEXT)";
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(createTableIfNotExists);
    }

    public static void addComponente(Context context, Componente componente) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, componente.getId());
        values.put(COLUMN_IMG, componente.getImg());
        values.put(COLUMN_NAME, componente.getName());
        values.put(COLUMN_BARCODE, componente.getBarcode());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public static List<Componente> getAllComponenti(Context context) {
        List<Componente> componenti = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definisce la selezione per escludere il componente con id = -1
        String selection = COLUMN_ID + " != ?";
        String[] selectionArgs = {"-1"};

        // Esegue la query per ottenere tutti i componenti tranne quello con id = -1
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String img = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String barcode = cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE));
                Componente componente = new Componente(id, img, name, barcode);
                componenti.add(componente);
                Log.d("COMPONENTETABLE", "getAllComponenti: " + componente.toString());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return componenti;
    }

    public static boolean isComponenteOnDB(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean componenteExists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return componenteExists;
    }

    public static boolean updateComponente(Context context, Componente componente) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IMG, componente.getImg());
        values.put(COLUMN_NAME, componente.getName());
        values.put(COLUMN_BARCODE, componente.getBarcode());

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(componente.getId())});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean deleteComponente(Context context, int id) {
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

    public static Componente getComponenteByName(Context context, String name) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definisce la selezione e gli argomenti della selezione per la query
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        // Esegue la query per ottenere il componente corrispondente al nome
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        Componente componente = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String img = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
            String barcode = cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE));

            componente = new Componente(id, img, name, barcode);
        }

        cursor.close();
        db.close();

        return componente;
    }

    public static Componente getComponenteById(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definisce la selezione e gli argomenti della selezione per la query
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Esegue la query per ottenere il componente corrispondente all'ID
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        Componente componente = null;
        if (cursor.moveToFirst()) {
            String img = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String barcode = cursor.getString(cursor.getColumnIndex(COLUMN_BARCODE));

            componente = new Componente(id, img, name, barcode);
        }

        cursor.close();
        db.close();

        return componente;
    }
}
