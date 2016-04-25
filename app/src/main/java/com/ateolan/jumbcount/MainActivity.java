package com.ateolan.jumbcount;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private class MenuDrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        /**
         * Swaps fragments in the main content view
         */
        private void selectItem(int position) {
            // Highlight the selected item, update the title, and close the drawer
            mainDrawerList.setItemChecked(position, true);
            setTitle(menuTitles[position]);
            mainDrawerLayout.closeDrawer(mainDrawerList);
        }

        public void setTitle(CharSequence title) {
            getSupportActionBar().setTitle(title);
        }
    }

    private static final int DOUBLE_PRESS_INTERVAL = 1000;
    private static final String TAG = MainActivity.class.getName();

    // Variables
    String[] menuTitles;
    int jumbCounter;
    long lastPressTime;
    Date today;
    String todayStr;

    // DBAccess
    JumbCounterDbHelper jcDbHelper;

    // Components
    TextView counterTV;
    Button plusButton;


    DrawerLayout mainDrawerLayout;
    ListView mainDrawerList;
    ActionBarDrawerToggle mainDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init
        jumbCounter = 0;
        counterTV = (TextView) findViewById(R.id.counterTV);
        plusButton = (Button) findViewById(R.id.plusButton);

        SimpleDateFormat sdf = new SimpleDateFormat(JumbCounterDbHelper.DATE_ONLY_FORMAT, Locale.getDefault());
        todayStr = sdf.format(new Date());

        jcDbHelper = new JumbCounterDbHelper(getApplicationContext());

        retrieveTodaysCounter();

        updateCounterTV();

        // Set up drawer
        menuTitles =  getResources().getStringArray(R.array.menu_titles_array);
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mainDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mainDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuTitles));
        // Set the list's click listener
        mainDrawerList.setOnItemClickListener(new MenuDrawerItemClickListener());

        mainDrawerToggle = new ActionBarDrawerToggle(this, mainDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getString(R.string.drawer_close));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.drawer_open));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mainDrawerLayout.addDrawerListener(mainDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mainDrawerToggle.syncState();
    }

    private void retrieveTodaysCounter() {
        SQLiteDatabase db = jcDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                JumbCounterStatistic.JumbEntry._ID,
                JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE,
                JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_COUNT
        };

        // Define 'where' part of query.
        String selection = JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE + " = ?";

        String[] selectionArgs = { todayStr };

        Cursor c = db.query(
                JumbCounterStatistic.JumbEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        if (c.getCount() != 0) {
            c.moveToFirst();

        }

        if (c.moveToFirst()) {
            jumbCounter = c.getInt(c.getColumnIndex(JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_COUNT));
            Log.d(TAG, "[retrieveTodaysCounter] Retrieved " + todayStr + " entry with value " + jumbCounter + ".");
        } else {
            jumbCounter = 0;
            Log.d(TAG, "[retrieveTodaysCounter] Did not find " + todayStr + " entry. Using value " + jumbCounter + ".");
        }

        c.close();
    }

    private void updateCounterDB() {
        SQLiteDatabase db = jcDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_COUNT, jumbCounter);

        // Which row to update, based on the ID
        String selection = JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE + " = ?";
        String[] selectionArgs = { todayStr };

        int count = db.update(
                JumbCounterStatistic.JumbEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.d(TAG, "[updateCounterDB] Updated " + count + " entries with value " + jumbCounter + ".");
    }

    private void insertCounterDB() {
        // Gets the data repository in write mode
        SQLiteDatabase db = jcDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE, todayStr);
        values.put(JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_COUNT, jumbCounter);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert (
                JumbCounterStatistic.JumbEntry.TABLE_NAME,
                null,
                values);

        Log.d(TAG, "[insertCounterDB] Inserting " + todayStr + " entry with value " + jumbCounter + ". Index is " + newRowId + ".");
    }

    private void deleteCounterDB() {
        // Gets the data repository in write mode
        SQLiteDatabase db = jcDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = JumbCounterStatistic.JumbEntry.COLUMN_NAME_INCIDENT_DATE + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { todayStr };

        // Issue SQL statement.
        db.delete(JumbCounterStatistic.JumbEntry.TABLE_NAME, selection, selectionArgs);

        Log.d(TAG, "[deleteCounterDB] Deleted " + todayStr + " entry with value " + jumbCounter + ".");
    }

    private void incrementCounterDB() {
        // Insert if zero
        if (jumbCounter == 1) {
            insertCounterDB();
        } else {
            updateCounterDB();
        }
    }

    private void decreaseCounterDB() {
        // Insert if zero
        if (jumbCounter == 0) {
            deleteCounterDB();
        } else {
            updateCounterDB();
        }
    }

    private void updateCounterTV() {
        counterTV.setText(getString(R.string.jumbcounter_val, jumbCounter));
    }

    public void minusButton_onClick(View v) {
        if (jumbCounter <= 0) {
            jumbCounter = 0;
            return;
        }

        long oldPressTime = lastPressTime;
        lastPressTime = System.currentTimeMillis();
        if (lastPressTime - oldPressTime > DOUBLE_PRESS_INTERVAL) {
            Toast.makeText(getApplicationContext(), "Press twice to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        lastPressTime = 0; // Reset
        jumbCounter--;

        updateCounterTV();
        decreaseCounterDB();
    }

    public void plusButton_onClick(View v) {
        jumbCounter++;
        updateCounterTV();
        incrementCounterDB();
    }
}
