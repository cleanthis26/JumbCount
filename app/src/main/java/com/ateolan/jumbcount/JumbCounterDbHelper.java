package com.ateolan.jumbcount;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cmetaxas on 25/04/2016.
 * Class for accessing the jumb entry
 */
public class JumbCounterDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + JumbCounterStatistic.JumbEntry.TABLE_NAME + " (" +
                    JumbCounterStatistic.JumbEntry._ID + " INTEGER PRIMARY KEY," +
                    JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE + TEXT_TYPE + COMMA_SEP +
                    JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_COUNT + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + JumbCounterStatistic.JumbEntry.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "JumbCount.db";

    public JumbCounterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for count data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
