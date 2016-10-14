package com.lilmanbigsolution.remindme;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by powce on 11/10/2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RemindMe";

    public static final String LIST_TABLE_NAME = "ListItems";

    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_LIST_ITEM_ID = "_id";
    public static final String COLUMN_LIST_ITEM = "ListItem";
    public static final String COLUMN_LIST_LOCATION = "Location";

    public DBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListTable = "CREATE TABLE " + LIST_TABLE_NAME + " ( " +
                COLUMN_LIST_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_ITEM + " TEXT, " + COLUMN_LIST_LOCATION + " TEXT);";

        db.execSQL(createListTable);

        ContentValues initialContent = new ContentValues();
        initialContent.put(COLUMN_LIST_ITEM, "Item Title");
        initialContent.put(COLUMN_LIST_LOCATION, "Location");
        db.insert(LIST_TABLE_NAME, null, initialContent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
