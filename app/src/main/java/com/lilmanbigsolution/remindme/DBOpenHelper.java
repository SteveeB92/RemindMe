package com.lilmanbigsolution.remindme;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by powce on 11/10/2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RemindMe";

    public static final String LIST_TABLE_NAME = "ListContent";

    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_LIST_CONTENT_ID = "ListContentID";
    public static final String COLUMN_LIST_CONTENT = "ListContent";

    public DBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListTable = "CREATE TABLE " + LIST_TABLE_NAME +
                COLUMN_LIST_CONTENT_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_LIST_CONTENT + " TEXT";

        db.execSQL(createListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
