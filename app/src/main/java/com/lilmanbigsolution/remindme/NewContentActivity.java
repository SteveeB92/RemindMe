package com.lilmanbigsolution.remindme;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class NewContentActivity extends AppCompatActivity {

    private long listItemId;

    public enum CallingSource {
        ADD (0),
        CHANGE (1);

        public int callingSource;

        private CallingSource(int callingSource){
            this.callingSource = callingSource;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDefaultValues();

        addNewContentItems(false, 0, false, null);

    }

    private void setDefaultValues(){
        //Did we get called from Add or Change?
        int callingSource = getIntent().getIntExtra("CallingSource", CallingSource.ADD.callingSource);

        if (callingSource == CallingSource.CHANGE.callingSource){
            //Get the values to default the values on the page
            listItemId = getIntent().getIntExtra("ListItemID", 0);

            String titleText = getIntent().getStringExtra("Title");
            EditText titleTextView = (EditText) findViewById(R.id.titleText);
            titleTextView.setText(titleText);
            titleTextView.setSelection(titleText.length());

            String locationText = getIntent().getStringExtra("Location");
            TextView locationView = (TextView) findViewById(R.id.locationText);
            locationView.setText(locationText);

            //Default the checkbox content lists
            DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
            SQLiteDatabase contentsDB = dbOpenHelper.getWritableDatabase();
            String[] contentsColumns = {dbOpenHelper.COLUMN_ID,
                    dbOpenHelper.COLUMN_LIST_ITEM_CONTENTS, dbOpenHelper.COLUMN_LIST_ITEM_CONTENT_COMPLETED};
            String whereClause = dbOpenHelper.COLUMN_LIST_ITEM_ID + "=" + listItemId;
            Cursor cursor = contentsDB.query(dbOpenHelper.LIST_CONTENTS_TABLE_NAME, contentsColumns,
                    whereClause, null, null, null, null );

            while (cursor.moveToNext()){
                int contentsID = cursor.getInt(cursor.getColumnIndex(dbOpenHelper.COLUMN_ID));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndex(dbOpenHelper.COLUMN_LIST_ITEM_CONTENT_COMPLETED)) == 1;
                String contents = cursor.getString(cursor.getColumnIndex(dbOpenHelper.COLUMN_LIST_ITEM_CONTENTS));

                addNewContentItems(false, contentsID, isCompleted, contents);
            }

        }
    }

    private void overideEnterKey(EditText editText){
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    //Add new layout beneath
                    addNewContentItems(true, 0, false, null);
                    return true;
                }

                return false;
            }
        });
    }

    private void addNewContentItems(boolean isSetFocus, int id, boolean isCompleted, String content){
        LinearLayout growingContentContainer = (LinearLayout) findViewById(R.id.growingContentContainer);

        LinearLayout newLinearLayout = new LinearLayout(this);
        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textViewID = new TextView(this);
        textViewID.setText(""+id);
        textViewID.setVisibility(View.GONE);

        CheckBox checkbox = new CheckBox(this);
        checkbox.setId(View.generateViewId());
        checkbox.setPressed(isCompleted);

        EditText contentText = new EditText(this);
        contentText.setEms(13);
        contentText.setId(View.generateViewId());
        contentText.setText(content);

        // Override the enter key on the edit text view
        overideEnterKey(contentText);
        newLinearLayout.addView(textViewID);
        newLinearLayout.addView(checkbox);
        newLinearLayout.addView(contentText);

        growingContentContainer.addView(newLinearLayout);

        if (isSetFocus)
            contentText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Save the content to the DB
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase contentsDB = dbOpenHelper.getWritableDatabase();

        EditText titleTextView = (EditText) findViewById(R.id.titleText);
        String titleText = titleTextView.getText().toString();

        TextView locationView = (TextView) findViewById(R.id.locationText);
        String locationText = locationView.getText().toString();

        //Create if id has not been created yet
        if (listItemId == 0)
            listItemId = dbOpenHelper.addNewListItem(contentsDB, titleText, locationText, null);
        else
            listItemId = dbOpenHelper.UpdateListItem(contentsDB, listItemId, titleText, locationText, null);

        //Iterate through children views of our growing container
        LinearLayout growingContentContainer = (LinearLayout) findViewById(R.id.growingContentContainer);
        for(int i = 0; i < growingContentContainer.getChildCount(); i++){
            LinearLayout individualLayout = (LinearLayout) growingContentContainer.getChildAt(i);

            TextView textView = (TextView) individualLayout.getChildAt(0);
            int listViewContentsID = Integer.parseInt(textView.getText().toString());

            CheckBox checkBox = (CheckBox) individualLayout.getChildAt(1);
            int isCompleted = checkBox.isPressed() ? 1 : 0;

            EditText contentEditText = (EditText) individualLayout.getChildAt(2);
            String contentString = contentEditText.getText().toString();

            //Create if id has not been created
            if (listViewContentsID == 0)
                dbOpenHelper.addNewListItemContent(contentsDB, listItemId, isCompleted, contentString);
            else
                dbOpenHelper.updateListItemContent(contentsDB, listViewContentsID, isCompleted, contentString);
        }
    }
}
