package com.example.stock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Items.db";
    //TABLE 1
    public static final String TABLE_NAME = "item_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "name";
    public static final String COL_2 = "price";
    public static final String COL_3 = "stock";
    public static final String COL_4 = "category";
    public static final String COL_5 = "promo";
    public static final String COL_6 = "description";
    public static final String COL_7 = "promoDescription";
    public static final String COL_8 = "promoStock";
    public static final String COL_9 = "promoPrice";
    public static final String COL_10 = "promoDiscount";


    //TABLE 3
    private static final String TABLE_NAME2 = "Report_table";
    private static final String Report_COL_0 = "StartingDate";
    private static final String Report_COL_1 = "EndingDate";
    private static final String Report_COL_2 = "Total";





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price INTEGER, stock INTEGER, category TEXT, promo TEXT, description TEXT, promoDescription TEXT," +
                "promoStock TEXT, promoPrice TEXT, promoDiscount TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME2 + " (StartingDate TEXT, EndingDate TEXT, Total INTEGER, Item TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        onCreate(db);
    }

    public void insertReport(String startingDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Report_COL_0, startingDate);
        contentValues.put(Report_COL_1, "-1");
        contentValues.put(Report_COL_2, 0);


        db.insert(TABLE_NAME2, null, contentValues);

    }
    public Cursor getAllDateData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME2 + " ORDER BY " + Report_COL_0 + " DESC", null);
        return data;
    }


    public Cursor getUnsettledDateData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME2 + " WHERE " + Report_COL_1 + " = '-1'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateTotal(String startingDate, int total){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME2 + " SET " + Report_COL_2 +
                " = '" + total + "' WHERE " + Report_COL_0 + " = '" + startingDate + "' AND " + Report_COL_1 + " = '-1'";
        db.execSQL(query);
    }


    public void settleTotal(String startingDate, String endingDate, int total){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME2 + " SET " + Report_COL_1 +
                " = '" + endingDate + "' ," + Report_COL_2 + " = '" + total + "' WHERE " + Report_COL_0 + " = '" + startingDate + "' AND " + Report_COL_1 + " = '-1'";
        db.execSQL(query);
    }


    public boolean insertData(String category, String name, String price, String stock, boolean promo, String description, String promoDescription, String promoStock, String promoPrice, String promoDiscount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String y;
        if (promo){
            y = "Y";
            contentValues.put(COL_7, promoDescription);
            contentValues.put(COL_8, promoStock);
            contentValues.put(COL_9, promoPrice);
            contentValues.put(COL_10, promoDiscount);
        }
        else{
            y = "N";
            contentValues.put(COL_7, "");
            contentValues.put(COL_8, "-1");
            contentValues.put(COL_9, "-1");
            contentValues.put(COL_10, "-1");
        }


        contentValues.put(COL_1, name);
        contentValues.put(COL_2, price);
        contentValues.put(COL_3, stock);
        contentValues.put(COL_4, category);
        contentValues.put(COL_5, y);
        contentValues.put(COL_6, description);


        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_4, null);
        return data;
    }
    public Cursor getItemNameByCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT " + COL_1 + " FROM " + TABLE_NAME +
                " WHERE " + COL_4 + " = '" + category + "'",null);
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_0 + " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDistinctItemCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT " + COL_4 + " FROM " + TABLE_NAME + " ORDER BY " + COL_4;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemIDByCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_0 + " FROM " + TABLE_NAME +
                " WHERE " + COL_4 + " = '" + category + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemStock(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_3 + " FROM " + TABLE_NAME +
                " WHERE " + COL_1 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getPromoForAllStatus(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_5 + ", " + COL_7 + ", " + COL_8 + ", " + COL_9 + ", " + COL_10 + " FROM " + TABLE_NAME +
                " WHERE " + COL_4 + " = '" + category + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public void updateName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_1 +
                " = '" + name + "' WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }
    public void updatePrice(int id, int price){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_2 +
                " = '" + price + "' WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }

    public void updateStock(int id, int delta){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_3 +
                " = '" + delta + "' WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }

    public void updateCategory(int id, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_4 +
                " = '" + category + "' WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }


    public void setPromoForAll(String category, boolean promoForAll){
        SQLiteDatabase db = this.getWritableDatabase();
        if (promoForAll){
            String query = "UPDATE " + TABLE_NAME + " SET " + COL_5 +
                    " = 'Yall' WHERE " +  COL_4 + " = '" + category + "'";
            db.execSQL(query);
        }


    }

    public void updatePromo(int id, boolean promo, String promoDescription, String promoStock, String promoPrice, String promoDiscount){
        SQLiteDatabase db = this.getWritableDatabase();

        if (promo){
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_5 +
                    " = 'Y' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_7 +
                    " = '" + promoDescription + "' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_8 +
                    " = '" + promoStock + "' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_9 +
                    " = '" + promoPrice + "' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_10 +
                    " = '" + promoDiscount + "' WHERE " +  COL_0 + " = '" + id + "'");
        }

        else{
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_5 +
                    " = 'N' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_7 +
                    " = '' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_8 +
                    " = '-1' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_9 +
                    " = '-1' WHERE " +  COL_0 + " = '" + id + "'");
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_10 +
                    " = '-1' WHERE " +  COL_0 + " = '" + id + "'");
        }
    }

    public void updateDescription(int id, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_6 +
                " = '" + description + "' WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }

    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +  COL_0 + " = '" + id + "'";
        db.execSQL(query);
    }




}
