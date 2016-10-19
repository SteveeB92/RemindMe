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
    public static final String LIST_CONTENTS_TABLE_NAME = "ListItemsContents";
    public static final String LIST_CONTENTS_VIEW_NAME = "ListItemsContentsView";

    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_LIST_ITEM_ID = "ListItemID";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LIST_ITEM = "ListItem";
    public static final String COLUMN_LIST_LOCATION = "Location";
    public static final String COLUMN_LIST_ITEM_CONTENTS_ID = "ListItemContentsID";
    public static final String COLUMN_LIST_ITEM_CONTENTS = "Contents";
    public static final String COLUMN_LIST_ITEM_CONTENT_COMPLETED = "IsCompleted";

    public DBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create List View table
        String createListTable = "CREATE TABLE " + LIST_TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_ITEM + " TEXT, " + COLUMN_LIST_LOCATION + " TEXT);";

        db.execSQL(createListTable);

        ContentValues initialListItems = new ContentValues();
        initialListItems.put(COLUMN_LIST_ITEM, "Item Title");
        initialListItems.put(COLUMN_LIST_LOCATION, "Location");
        long listItemID = db.insert(LIST_TABLE_NAME, null, initialListItems);

        //Create the table to hold the contents for the list view items
        String createListContentsTable = "CREATE TABLE " + LIST_CONTENTS_TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_ITEM_ID + " INTEGER, " + COLUMN_LIST_ITEM_CONTENTS + " TEXT, " +
                COLUMN_LIST_ITEM_CONTENT_COMPLETED + " TEXT);";

        db.execSQL(createListContentsTable);

        ContentValues initialContent = new ContentValues();
        initialContent.put(COLUMN_LIST_ITEM_ID, listItemID);
        initialContent.put(COLUMN_LIST_ITEM_CONTENTS, "Contents");
        initialContent.put(COLUMN_LIST_ITEM_CONTENT_COMPLETED, "1");
        db.insert(LIST_CONTENTS_TABLE_NAME, null, initialContent);
/*
        //Bring both of these tables together to form a view
        String createListTableView = "CREATE VIEW " + LIST_CONTENTS_VIEW_NAME + " AS SELECT " +
                LIST_TABLE_NAME + "." + COLUMN_ID + ", " + COLUMN_LIST_ITEM + ", " +
                COLUMN_LIST_LOCATION + ", " + COLUMN_LIST_ITEM_CONTENTS_ID + ", " +
                COLUMN_LIST_ITEM_CONTENTS + ", " + COLUMN_LIST_ITEM_CONTENT_COMPLETED + " " +
                "FROM " + LIST_TABLE_NAME + " INNER JOIN " + LIST_CONTENTS_TABLE_NAME + " ON " +
                LIST_TABLE_NAME + "." + COLUMN_LIST_ITEM_ID  +" = " + LIST_CONTENTS_TABLE_NAME + "."
                + COLUMN_LIST_ITEM_ID;

        db.execSQL(createListTableView);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    /**
     * Create a new list item and return the id (-1 if errored)
     * @param title
     * @param location
     * @param coordinates
     * @return int - the created List Item ID (-1 if errored)
     */
    public long addNewListItem(SQLiteDatabase db, String title, String location, String coordinates) {
        ContentValues listItem = new ContentValues();
        listItem.put(COLUMN_LIST_ITEM, title);
        listItem.put(COLUMN_LIST_LOCATION, location);
        return db.insert(LIST_TABLE_NAME, null, listItem);
    }

    /**
     * Change a list item and return the id (-1 if errored)
     * @param title
     * @param location
     * @param coordinates
     * @return int - the changed List Item ID (-1 if errored)
     */
    public long UpdateListItem(SQLiteDatabase db, long listItemID, String title, String location, String coordinates) {
        ContentValues listItem = new ContentValues();
        listItem.put(COLUMN_LIST_ITEM, title);
        listItem.put(COLUMN_LIST_LOCATION, location);
        return db.update(LIST_TABLE_NAME, listItem, COLUMN_ID + "=" + listItemID, null);
    }

    /**
     * Create a new list item Content and return the id (-1 if errored)
     * @param db
     * @param listItemID
     * @param isChecked
     * @param content
     * @return int - the created List Item Content ID (-1 if errored)
     */
    public long addNewListItemContent(SQLiteDatabase db, long listItemID, int isChecked, String content) {
        ContentValues listItemContents = new ContentValues();
        listItemContents.put(COLUMN_LIST_ITEM_ID, listItemID);
        listItemContents.put(COLUMN_LIST_ITEM_CONTENT_COMPLETED, isChecked);
        listItemContents.put(COLUMN_LIST_ITEM_CONTENTS, content);
        return db.insert(LIST_CONTENTS_TABLE_NAME, null, listItemContents);
    }


    /**
     * Update a list item Contents and return the id (-1 if errored)
     * @param db
     * @param listItemContentsID
     * @param isChecked
     * @param content
     * @return int - the updated List Item Content ID (-1 if errored)
     */
    public long updateListItemContent(SQLiteDatabase db, long listItemContentsID, int isChecked, String content) {
        ContentValues listItemContents = new ContentValues();
        listItemContents.put(COLUMN_LIST_ITEM_CONTENT_COMPLETED, isChecked);
        listItemContents.put(COLUMN_LIST_ITEM_CONTENTS, content);
        return db.update(LIST_CONTENTS_TABLE_NAME, listItemContents, COLUMN_ID + "=" + listItemContentsID, null);
    }
}
