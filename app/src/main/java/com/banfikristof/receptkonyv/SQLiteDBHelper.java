package com.banfikristof.receptkonyv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    //DB Info
    public static final String DB_NAME = "Receptkonyv";
    public static final String TABLE_NAME = "Receptek";

    //Oszlopok
    private static final String[] COL = {
            "id",
            "nev",
            "hozzavalok",
            "elkeszites"
    };

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE ? (" +
                        "? INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "? TEXT," +
                        "? TEXT," +
                        "? TEXT," +
                        ")",
                new String[]{TABLE_NAME,COL[0],COL[1],COL[2],COL[3]}
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
