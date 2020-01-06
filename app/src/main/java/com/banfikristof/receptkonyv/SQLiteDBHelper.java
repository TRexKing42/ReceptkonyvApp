package com.banfikristof.receptkonyv;

import android.content.ContentValues;
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
    public static final String[] COL = {
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

    public long newRecipe(String nev, String leiras, String hozzavalok, String elkeszites){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nev",nev);
        cv.put("leiras",leiras);
        cv.put("hozzavalok",hozzavalok);
        cv.put("elkeszites",elkeszites);
        return database.insert(TABLE_NAME,null,cv);
    }

    public long newRecipe(Recipe r){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nev",r.getName());
        cv.put("leiras",r.getDescription());
        cv.put("hozzavalok",r.getIngredientsString());
        cv.put("elkeszites",r.getPreparation());
        return database.insert(TABLE_NAME,null,cv);
    }

    public boolean deleteRecipe(String id){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        long result = database.delete(TABLE_NAME,"id = ?",new String[]{id});

        return result != 0;
    }
}
