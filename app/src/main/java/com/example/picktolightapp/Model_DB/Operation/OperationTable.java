package com.example.picktolightapp.Model_DB.Operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.picktolightapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class OperationTable {

    public static final String TABLE_NAME = "operations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL)";

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
                COLUMN_NAME + " TEXT NOT NULL)";
        db.execSQL(createTableIfNotExists);
    }


    // Add a new operation
    public static void addOperation(Context context, Operation operation) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, operation.getId());
        values.put(COLUMN_NAME, operation.getName());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static void addAllOperations(Context context) {
        for (Operation operation : Operation.values()) {
            addOperation(context, operation);
        }
    }

    // Retrieve all operations
    public static List<Operation> getAllOperations(Context context) {
        List<Operation> operations = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                try {
                    Operation operation = Operation.getById(id);
                    operations.add(operation);
                    Log.d("OPERATIONTABLE", "getAllOperations: " + operation.toString());
                } catch (IllegalArgumentException e) {
                    Log.e("OPERATIONTABLE", "Unknown operation ID: " + id);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return operations;
    }

    // Check if an operation exists in the database by name
    public static boolean isOperationOnDB(Context context, String name) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean operationExists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return operationExists;
    }

    // Update operation name by ID
    public static boolean updateOperationName(Context context, int id, String newName) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return rowsAffected > 0;
    }

    // Delete an operation by ID
    public static boolean deleteOperation(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();

        return rowsDeleted > 0;
    }

    // Clear all operations
    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
