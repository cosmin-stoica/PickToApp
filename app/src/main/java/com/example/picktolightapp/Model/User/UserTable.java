package com.example.picktolightapp.Model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserTable {

    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIPO + " INTEGER, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " COLUMN_USERNAME)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIPO + " INTEGER, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableIfNotExists);
    }

    public static void addUser(Context context,  TipoUser tipo, String username, String pass) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIPO, tipo.getCodice());
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, pass);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static List<User> getAllUsers(Context context) {
        List<User> users = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TipoUser tipo = TipoUser.fromCodice(cursor.getInt(cursor.getColumnIndex(COLUMN_TIPO)));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                if(name.equals("Root"))
                    continue;
                String pass = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
                User toAdd = new User(tipo,name, pass);
                users.add(toAdd);
                Log.d("USERTABLE", "getAllUsers: " + toAdd.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public static List<User> getAllUsersNoRoot(Context context) {
        List<User> users = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TipoUser tipo = TipoUser.fromCodice(cursor.getInt(cursor.getColumnIndex(COLUMN_TIPO)));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                String pass = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
                User toAdd = new User(tipo,name, pass);
                users.add(toAdd);
                Log.d("USERTABLE", "getAllUsers: " + toAdd.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public static boolean isUserOnDB(Context context, String username) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_USERNAME + " = ? ";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return userExists;
    }

    public static TipoUser getTipoUserbyUser(Context context, String username) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a selection criteria
        String selection = COLUMN_USERNAME + " = ? ";
        String[] selectionArgs = { username };


        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_TIPO},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        TipoUser tipoUser = null;

        if (cursor != null && cursor.moveToFirst()) {
            int tipoCode = cursor.getInt(cursor.getColumnIndex(COLUMN_TIPO));
            tipoUser = TipoUser.fromCodice(tipoCode);
            cursor.close();
        }

        db.close();
        return tipoUser;
    }

    public static String getPasswordbyUser(Context context, String username) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String password="";
        // Define a selection criteria
        String selection = COLUMN_USERNAME + " = ? ";
        String[] selectionArgs = { username };


        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_PASSWORD},
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            cursor.close();
        }

        db.close();
        return password;
    }



    public static boolean addUserIfNotExists(Context context, TipoUser tipo, String username, String password){

        if(!isUserOnDB(context,username)){
            addUser(context,tipo,username,password);
            return true;
        }
        else{
            return false;
        }

    }

    public static boolean changePasswordByUsername(Context context, String username, String newPassword) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean changeUsernameByUsername(Context context, String oldUsername, String newUsername) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(isUserOnDB(context,newUsername)){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_USERNAME + " = ?", new String[]{oldUsername});
        db.close();

        return rowsAffected > 0;
    }

    public static boolean changeTipoByUsername(Context context, String username, TipoUser tipoUser) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIPO, tipoUser.getCodice());

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();

        return rowsAffected > 0;
    }


    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public static boolean deleteUser(Context context, String username) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        int rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();

        return rowsDeleted > 0;
    }
}
