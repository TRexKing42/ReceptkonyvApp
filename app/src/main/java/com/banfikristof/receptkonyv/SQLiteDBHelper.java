package com.banfikristof.receptkonyv;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    //DB Info
    public static final String DB_NAME = "Receptkonyv";
    public static final String TABLE_NAME = "Receptek";

    //Oszlopok
    private static final String[] COL = {
            "id",
            "nev",
            "leiras",
            "hozzavalok",
            "elkeszites"
    };

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " +TABLE_NAME+ " (" +
                        COL[0]+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL[1]+ " TEXT, " +
                        COL[2]+ " TEXT, " +
                        COL[3]+ " TEXT, " +
                        COL[4]+ " TEXT " +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return result;
    }
}
