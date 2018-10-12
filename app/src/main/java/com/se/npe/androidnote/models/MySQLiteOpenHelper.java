package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.se.npe.androidnote.models.TableConfig;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static  MySQLiteOpenHelper helper;

    private MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private MySQLiteOpenHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    public static  synchronized  MySQLiteOpenHelper getInstance(Context context) {
        if(helper==null){
            helper = new MySQLiteOpenHelper(context, "Notes");
        }
        return  helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists "+TableConfig.TABLE_NAME+"("
                +TableConfig.Note.NOTE_ID+" integer not null primary key autoincrement,"
                +TableConfig.Note.NOTE_TITLE+ " verchar(50),"
                +TableConfig.Note.NOTE_CONTENT+ " TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}