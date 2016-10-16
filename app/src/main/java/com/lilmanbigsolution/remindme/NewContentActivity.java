package com.lilmanbigsolution.remindme;

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

    public enum CallingSource {
        ADD (0),
        CHANGE (1);

        private int callingSource;

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

        addNewContentItems(0, false, null);

    }

    private void setDefaultValues(){
        //Did we get called from Add or Change?
        int callingSource = getIntent().getIntExtra("CallingSource", CallingSource.ADD.callingSource);

        if (callingSource == CallingSource.CHANGE.callingSource){
            //Get the values to default the values on the page
            String titleText = getIntent().getStringExtra("Title");
            EditText titleTextView = (EditText) findViewById(R.id.titleText);
            titleTextView.setText(titleText);

            String locationText = getIntent().getStringExtra("Location");
            TextView locationView = (TextView) findViewById(R.id.locationText);
            locationView.setText(locationText);

            //TODO Default the checkbox lists

        }
    }

    private void overideEnterKey(EditText editText){
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    //Add new layout beneath
                    addNewContentItems(0, false, null);
                    return true;
                }

                return false;
            }
        });
    }

    private void addNewContentItems(int id, boolean isCompleted, String content){
        LinearLayout growingContentContainer = (LinearLayout) findViewById(R.id.growingContentContainer);

        LinearLayout newLinearLayout = new LinearLayout(this);
        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textViewID = new TextView(this);
        textViewID.setText(id);
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
        long listItemID = dbOpenHelper.addNewListItem(contentsDB, titleText, locationText, null);

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
                dbOpenHelper.addNewListItemContent(contentsDB, listItemID, isCompleted, contentString);
            else
                dbOpenHelper.updateListItemContent(contentsDB, listViewContentsID, isCompleted, contentString);
        }
    }
}
