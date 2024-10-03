package com.example.picktolightapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.picktolightapp.Model_DB.Componente.ComponenteTable;
import com.example.picktolightapp.Model_DB.DispositiviComponenti.DispositiviComponentiTable;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStorico;
import com.example.picktolightapp.Model_DB.DispositivoStorico.DispositivoStoricoTable;
import com.example.picktolightapp.Model_DB.Event.EventoTable;
import com.example.picktolightapp.Model_DB.Global.GlobalTable;
import com.example.picktolightapp.Model_DB.Operation.OperationTable;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.UserTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        UserTable.onCreate(db);
        EventoTable.onCreate(db);
        OperationTable.onCreate(db);
        PermissionOperationsTable.onCreate(db);
        DispositivoTable.onCreate(db);
        GlobalTable.onCreate(db);
        ComponenteTable.onCreate(db);
        //DispositiviComponentiTable.onCreate(db);
        DispositivoStoricoTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        UserTable.onUpgrade(db, oldVersion, newVersion);
        EventoTable.onUpgrade(db, oldVersion,newVersion);
        OperationTable.onUpgrade(db,oldVersion,newVersion);
        PermissionOperationsTable.onUpgrade(db,oldVersion,newVersion);
        DispositivoTable.onUpgrade(db,oldVersion,newVersion);
        GlobalTable.onUpgrade(db,oldVersion,newVersion);
        ComponenteTable.onUpgrade(db,oldVersion,newVersion);
        //DispositiviComponentiTable.onUpgrade(db,oldVersion,newVersion);
        DispositivoStoricoTable.onUpgrade(db,oldVersion,newVersion);
    }

    public void refreshTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");
        UserTable.createTable(db);
        EventoTable.createTable(db);
        OperationTable.createTable(db);
        PermissionOperationsTable.createTable(db);
        DispositivoTable.createTable(db);
        GlobalTable.createTable(db);
        ComponenteTable.createTable(db);
        //DispositiviComponentiTable.createTable(db);
        DispositivoStoricoTable.createTable(db);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");

        Cursor cursor = db.rawQuery("PRAGMA foreign_keys;", null);
        if (cursor.moveToFirst()) {
            int foreignKeysEnabled = cursor.getInt(0);
            if (foreignKeysEnabled == 1) {
                Log.d("DatabaseHelper", "Foreign keys sono abilitati.");
            } else {
                Log.e("DatabaseHelper", "Foreign keys NON sono abilitati!");
            }
        }
        cursor.close();
        return db;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");

        Cursor cursor = db.rawQuery("PRAGMA foreign_keys;", null);
        if (cursor.moveToFirst()) {
            int foreignKeysEnabled = cursor.getInt(0);
            if (foreignKeysEnabled == 1) {
                Log.d("DatabaseHelper", "Foreign keys sono abilitati.");
            } else {
                Log.e("DatabaseHelper", "Foreign keys NON sono abilitati!");
            }
        }
        cursor.close();
        return db;
    }

}
