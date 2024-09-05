package com.example.picktolightapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.picktolightapp.Model.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model.Event.EventoTable;
import com.example.picktolightapp.Model.Global.GlobalTable;
import com.example.picktolightapp.Model.Operation.OperationTable;
import com.example.picktolightapp.Model.PermissionOperationsTable;
import com.example.picktolightapp.Model.User.UserTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        UserTable.onCreate(db);
        EventoTable.onCreate(db);
        OperationTable.onCreate(db);
        PermissionOperationsTable.onCreate(db);
        DispositivoTable.onCreate(db);
        GlobalTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        UserTable.onUpgrade(db, oldVersion, newVersion);
        EventoTable.onUpgrade(db, oldVersion,newVersion);
        OperationTable.onUpgrade(db,oldVersion,newVersion);
        PermissionOperationsTable.onUpgrade(db,oldVersion,newVersion);
        DispositivoTable.onUpgrade(db,oldVersion,newVersion);
        GlobalTable.onUpgrade(db,oldVersion,newVersion);
    }

    public void refreshTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        UserTable.createTable(db);
        EventoTable.createTable(db);
        OperationTable.createTable(db);
        PermissionOperationsTable.createTable(db);
        DispositivoTable.createTable(db);
        GlobalTable.createTable(db);
    }
}
