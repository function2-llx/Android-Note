package com.se.npe.androidnote.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.se.npe.androidnote.models.TableConfig;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static MySQLiteOpenHelper helper;

    private MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private MySQLiteOpenHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    public static synchronized MySQLiteOpenHelper getInstance(Context context) {
        if (helper == null) {
            helper = new MySQLiteOpenHelper(context, "Notes");
        }
        return helper;
    }

    /**
     * Called when the database is created for the first time.
     * Create tables and initialize.
     *
     * @param sqLiteDatabase The SQLite database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TableConfig.TABLE_NAME + "("
                + TableConfig.Note.NOTE_ID + " integer not null primary key autoincrement,"
                + TableConfig.Note.NOTE_TITLE + " verchar(50),"
                + TableConfig.Note.NOTE_CONTENT + " TEXT)");
    }

    /**
     * Called when the database needs to be upgraded from an old version to a new version.
     * Drop tables, add tables, alter tables, or do anything else to upgrade to the new version.
     *
     * @param sqLiteDatabase The SQLite database.
     * @param oldVersion     The old database version.
     * @param newVersion     The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Work well in early development
        sqLiteDatabase.execSQL("drop table if exists " + TableConfig.TABLE_NAME);
        onCreate(sqLiteDatabase);

        // Work better in production
        // if (oldVersion < 2) {
        //     sqLiteDatabase.execSQL("alter table " + TableConfig.TABLE_NAME +
        //             " add column " + "ColumnName1" + " string;");
        // }
        // if (oldVersion < 3) {
        //     sqLiteDatabase.execSQL("alter table " + TableConfig.TABLE_NAME +
        //             " add column " + "ColumnName2" + " string;");
        // }
    }
}