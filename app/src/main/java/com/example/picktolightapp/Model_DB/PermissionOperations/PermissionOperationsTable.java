package com.example.picktolightapp.Model_DB.PermissionOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;

import com.example.picktolightapp.DatabaseHelper;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.User.User;

import java.util.ArrayList;
import java.util.List;

public class PermissionOperationsTable {

    public static final String TABLE_NAME = "permission_operations";
    public static final String COLUMN_USER_TYPE = "user_type";  // Changed to String type
    public static final String COLUMN_ID_OPERATION = "id_operation"; // Still an Integer referencing operations

    // SQL statement to create the updated permission_operations table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_USER_TYPE + " TEXT, " +
                    COLUMN_ID_OPERATION + " INTEGER, " +
                    "PRIMARY KEY (" + COLUMN_USER_TYPE + ", " + COLUMN_ID_OPERATION + "), " +
                    "FOREIGN KEY (" + COLUMN_ID_OPERATION + ") REFERENCES operations(id) ON DELETE CASCADE)";

    // Create the table
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade the table
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Create the table if it doesn't exist
    public static void createTable(SQLiteDatabase db) {
        String createTableIfNotExists = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_USER_TYPE + " TEXT, " +
                COLUMN_ID_OPERATION + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_USER_TYPE + ", " + COLUMN_ID_OPERATION + "), " +
                "FOREIGN KEY (" + COLUMN_ID_OPERATION + ") REFERENCES operations(id) ON DELETE CASCADE)";
        db.execSQL(createTableIfNotExists);
    }

    // Add a new relation between user type and operation
    public static void addPermissionOperation(Context context, String userType, Operation operation) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_TYPE, userType);
        values.put(COLUMN_ID_OPERATION, operation.getId());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve all operations associated with a specific user type
    public static List<Integer> getOperationsForUserType(Context context, String userType) {
        List<Integer> operationIds = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_USER_TYPE + " = ?";
        String[] selectionArgs = {userType};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID_OPERATION}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int operationId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_OPERATION));
                operationIds.add(operationId);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return operationIds;
    }

    // Retrieve all user types associated with a specific operation
    public static List<String> getUserTypesForOperation(Context context, int operationId) {
        List<String> userTypes = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_ID_OPERATION + " = ?";
        String[] selectionArgs = {String.valueOf(operationId)};

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_USER_TYPE}, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String userType = cursor.getString(cursor.getColumnIndex(COLUMN_USER_TYPE));
                userTypes.add(userType);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userTypes;
    }

    // Delete a specific user type-operation association
    public static boolean deletePermissionOperation(Context context, String userType, int operationId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = COLUMN_USER_TYPE + " = ? AND " + COLUMN_ID_OPERATION + " = ?";
        String[] selectionArgs = {userType, String.valueOf(operationId)};

        int rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();

        return rowsDeleted > 0;
    }

    // Clear all user type-operation associations
    public static void clearAll(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public static boolean userHasPermission(Context context, User user, Operation operation, boolean seeWarning) {
        String userType = user.getTipo().getCodiceString();
        int operationId = operation.getId();

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = COLUMN_USER_TYPE + " = ? AND " + COLUMN_ID_OPERATION + " = ?";
        String[] selectionArgs = { userType, String.valueOf(operationId) };

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean hasPermission = cursor.getCount() > 0;

        cursor.close();
        db.close();

        if(!hasPermission && seeWarning) {
            LayoutInflater inflater = LayoutInflater.from(context);
            DialogsHandler.showWarningDialog(
                    context,
                    inflater,
                    "Warning",
                    "Non hai i permessi necessari",
                    null
            );
        }

        return hasPermission;
    }
}
