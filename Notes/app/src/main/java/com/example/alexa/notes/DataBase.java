package com.example.alexa.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexa on 07.02.2018.
 */

public class DataBase {
    private final static String DB_NAME = "NotesDB";
    private final static String DB_TABLE = "Notes";
    private final static int DB_VERSION = 1;

    final static String COLUMN_ID = "_id";
    final static String COLUMN_TITLE = "title";
    final static String COLUMN_CONTENT = "content";
    final static String COLUMN_PRIORITY = "priority";
    final static String COLUMN_LONGTITUDE = "gps_longtitude";
    final static String COLUMN_LINTITIDE = "gps_lintitude";
    final static String COLUMN_DATE_CREATE_EDIT = "date";

    private final static String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement not null, " +
                    COLUMN_TITLE + " text, " +
                    COLUMN_CONTENT + " char(255), " +
                    COLUMN_PRIORITY + " integer, " +
                    COLUMN_LINTITIDE + " double, " +
                    COLUMN_LONGTITUDE + " double, " +
                    COLUMN_DATE_CREATE_EDIT + " text" +
            ");";

    private final Context contextDB;
    private SQLiteDatabase dbSqLiteDatabase;
    private DBHelper dbHelper;

    public DataBase(Context contextDB) {
        this.contextDB = contextDB;
    }

    public void open_connection(){
        dbHelper = new DBHelper(contextDB, DB_NAME, null, DB_VERSION);
        dbSqLiteDatabase = dbHelper.getWritableDatabase();
    }
    public void close_connection(){
        if (dbHelper != null){
            dbHelper.close();
        }
    }

    public Cursor getEntry(long id){
        return dbSqLiteDatabase.query(   DB_TABLE,
                                        null,
                                        COLUMN_ID + " = " + id,
                                        null,
                                        null,
                                        null,
                                        null
                                      );
    }

    public Cursor getEntries(){
        return dbSqLiteDatabase.query(DB_TABLE,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null ,
                                    COLUMN_DATE_CREATE_EDIT + " DESC");
    }

    public void addToDB(String title, String content, int priority, double lintitude, double longtitude, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_CONTENT, content);
        contentValues.put(COLUMN_PRIORITY, priority);
        contentValues.put(COLUMN_LINTITIDE, lintitude);
        contentValues.put(COLUMN_LONGTITUDE, longtitude);
        contentValues.put(COLUMN_DATE_CREATE_EDIT, date);
        dbSqLiteDatabase.insert(DB_TABLE, null, contentValues);
    }

    public void updateDB(int id, String title, String content, int priority, double lintitude, double longtitude, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_CONTENT, content);
        contentValues.put(COLUMN_PRIORITY, priority);
        contentValues.put(COLUMN_LINTITIDE, lintitude);
        contentValues.put(COLUMN_LONGTITUDE, longtitude);
        contentValues.put(COLUMN_DATE_CREATE_EDIT, date);
        dbSqLiteDatabase.update(DB_TABLE,
                                contentValues,
                                COLUMN_ID + " = " + id,
                                null);
    }

    public void deleteDB(long id){
        dbSqLiteDatabase.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // класс по созданию и управлению БД
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
