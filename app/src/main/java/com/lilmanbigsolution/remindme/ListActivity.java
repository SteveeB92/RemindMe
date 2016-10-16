package com.lilmanbigsolution.remindme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    SQLiteDatabase contentsDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openDatabase();
        populateListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewListIntent = new Intent(ListActivity.this, NewContentActivity.class);
                ListActivity.this.startActivity(createNewListIntent);
            }
        });

        /*ImageButton locationButton = (ImageButton) findViewById(R.id.locationImageButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment mapFragment = MapFragment.newInstance();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.add(R.id.mapFragment, mapFragment);
                fragmentTransaction.commit();
            }
        });*/

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        populateListView();
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

    private void populateListView()
    {
        String[] columnNames = {DBOpenHelper.COLUMN_ID, DBOpenHelper.COLUMN_LIST_ITEM, DBOpenHelper.COLUMN_LIST_LOCATION};
        int[] layoutViews = {R.id.hiddenListItemID, R.id.titleText, R.id.locationText};
        Cursor cursor = contentsDB.query(DBOpenHelper.LIST_TABLE_NAME, columnNames, null, null, null, null, null);
        SimpleCursorAdapter listContentAdapter = new SimpleCursorAdapter(this, R.layout.main_list_item_view, cursor,
                columnNames, layoutViews, 0);

        ListView listView = (ListView) findViewById(R.id.contentListView);
        listView.setAdapter(listContentAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent createNewListIntent = new Intent(ListActivity.this, NewContentActivity.class);
                ListActivity.this.startActivity(createNewListIntent);
            }
        });
    }
}
