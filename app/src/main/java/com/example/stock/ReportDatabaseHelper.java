package com.example.stock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ReportDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Report.db";
    public static final String TABLE_NAME = "report_table";
    public static final String COL_0 = "Date";
    public static final String COL_1 = "";
    public static final String COL_2 = "Total";
    public static final String COL_3 = "stock";
    public static final String COL_4 = "category";

    public ReportDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (Date TEXT, name TEXT, price INTEGER, stock INTEGER, category TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
