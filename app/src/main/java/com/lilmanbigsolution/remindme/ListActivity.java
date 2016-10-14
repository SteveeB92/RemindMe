package com.lilmanbigsolution.remindme;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.app.*;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.MapFragment;

public class ListActivity extends AppCompatActivity {

    SQLiteDatabase contentsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openDatabase();
        SimpleCursorAdapter listContentAdapter = retrieveDatabaseData();

        ListView listView = (ListView) findViewById(R.id.contentListView);
        listView.setAdapter(listContentAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        ImageButton locationButton = (ImageButton) findViewById(R.id.locationImageButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment mapFragment = MapFragment.newInstance();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.add(R.id.mapFragment, mapFragment);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickLocationManager()
    {

    }

    private void openDatabase(){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
        contentsDB = dbOpenHelper.getWritableDatabase();
    }

    private SimpleCursorAdapter retrieveDatabaseData()
    {
        String[] columnNames = {DBOpenHelper.COLUMN_LIST_CONTENT_ID, DBOpenHelper.COLUMN_LIST_CONTENT };
        contentsDB.query(DBOpenHelper.LIST_TABLE_NAME, columnNames, null, null, null, null, null);
        //TODO
        return null;
    }
}
