package com.lilmanbigsolution.remindme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
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
}
