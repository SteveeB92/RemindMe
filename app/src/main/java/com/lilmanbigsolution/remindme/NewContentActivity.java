package com.lilmanbigsolution.remindme;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsSatellite;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class NewContentActivity extends AppCompatActivity implements OnMapReadyCallback {

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

        //Override Location view to open Maps on click
        addOnClickToLocationText();
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
            String[] contentsColumns = {DBOpenHelper.COLUMN_ID,
                    DBOpenHelper.COLUMN_LIST_ITEM_CONTENTS, DBOpenHelper.COLUMN_LIST_ITEM_CONTENT_COMPLETED};
            String whereClause = DBOpenHelper.COLUMN_LIST_ITEM_ID + "=" + listItemId;
            Cursor cursor = contentsDB.query(DBOpenHelper.LIST_CONTENTS_TABLE_NAME, contentsColumns,
                    whereClause, null, null, null, null );

            while (cursor.moveToNext()){
                int contentsID = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST_ITEM_CONTENT_COMPLETED)) == 1;
                String contents = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST_ITEM_CONTENTS));

                addNewContentItems(false, contentsID, isCompleted, contents);
            }
            cursor.close();
        }
    }

    private void overrideEnterKey(EditText editText){
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    //Do we need a new layout?
                    LinearLayout parentLayout = (LinearLayout) v.getParent();
                    LinearLayout parentParentLayout = (LinearLayout) parentLayout.getParent();
                    int indexOfParent = parentParentLayout.indexOfChild(parentLayout);

                    if(indexOfParent == parentParentLayout.getChildCount() - 1) {
                        //Add new layout beneath
                        addNewContentItems(true, 0, false, null);
                    }
                    else {
                        //Put focus in next view
                        LinearLayout nextParentLayout = (LinearLayout) parentParentLayout.getChildAt(indexOfParent+1);
                        EditText contentsText = (EditText) nextParentLayout.getChildAt(2);
                        contentsText.requestFocus();
                        contentsText.setSelection(contentsText.getText().toString().length());
                    }
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
        checkbox.setChecked(isCompleted);
        EditText contentText = new EditText(this);
        contentText.setEms(13);
        contentText.setId(View.generateViewId());
        contentText.setText(content);
        contentText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        contentText.setSingleLine(false);

        // Override the enter key on the edit text view
        overrideEnterKey(contentText);
        newLinearLayout.addView(textViewID);
        newLinearLayout.addView(checkbox);
        newLinearLayout.addView(contentText);

        growingContentContainer.addView(newLinearLayout);

        if (isSetFocus)
            contentText.requestFocus();
    }

    private void addOnClickToLocationText() {
        //Override Location view to open Maps on click
        TextView locationView = (TextView) findViewById(R.id.locationText);
        locationView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View locationView) {
                //RelativeLayout parentLayout = (RelativeLayout) locationView.getParent();
                MapFragment mapFragment = MapFragment.newInstance();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.newListContainer, mapFragment);
                fragmentTransaction.commit();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(50.8136290, -0.1013630)));

                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                        googleMap.animateCamera(zoom);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        commitContents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                commitContents();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void commitContents(){
        //Save the content to the DB
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase contentsDB = dbOpenHelper.getWritableDatabase();

        EditText titleTextView = (EditText) findViewById(R.id.titleText);
        String titleText = titleTextView.getText().toString();

        if (titleText.equals(""))
            return;

        TextView locationView = (TextView) findViewById(R.id.locationText);
        String locationText = locationView.getText().toString();

        //Create if id has not been created yet
        if (listItemId == 0)
            listItemId = dbOpenHelper.addNewListItem(contentsDB, titleText, locationText, null);
        else
            dbOpenHelper.UpdateListItem(contentsDB, listItemId, titleText, locationText, null);

        //Iterate through children views of our growing container
        LinearLayout growingContentContainer = (LinearLayout) findViewById(R.id.growingContentContainer);
        for(int i = 0; i < growingContentContainer.getChildCount(); i++){
            LinearLayout individualLayout = (LinearLayout) growingContentContainer.getChildAt(i);

            TextView textView = (TextView) individualLayout.getChildAt(0);
            int listViewContentsID = Integer.parseInt(textView.getText().toString());

            CheckBox checkBox = (CheckBox) individualLayout.getChildAt(1);
            int isCompleted = checkBox.isChecked() ? 1 : 0;

            EditText contentEditText = (EditText) individualLayout.getChildAt(2);
            String contentString = contentEditText.getText().toString();

            if (contentString.equals(""))
                continue;

            //Create if id has not been created
            if (listViewContentsID == 0)
                dbOpenHelper.addNewListItemContent(contentsDB, listItemId, isCompleted, contentString);
            else
                dbOpenHelper.updateListItemContent(contentsDB, listViewContentsID, isCompleted, contentString);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapOptions options = new GoogleMapOptions();

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(50.8136290, -0.1013630)));
    }
}
