package com.badboy.gpstracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.badboy.gpstracker.adapter.TripListAdapter;
import com.badboy.gpstracker.db.DBHelper;
import com.badboy.gpstracker.model.LocationObj;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TripListAdapter.RecyclerViewItemClickListener {

    private RecyclerView rvTripDateList;
    private TripListAdapter tripListAdapter;
    private List<LocationObj> locationObjList;
    DBHelper dbHelper;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);


        locationObjList = dbHelper.getDataByDate();
        rvTripDateList = (RecyclerView) findViewById(R.id.rvTripList);
        mLayoutManager = new LinearLayoutManager(this);
        rvTripDateList.setLayoutManager(mLayoutManager);
        // if (locationObjList != null && locationObjList.size() > 0) {

        tripListAdapter = new TripListAdapter(locationObjList, this);
        rvTripDateList.setAdapter(tripListAdapter);
        Log.d("count", locationObjList.size() + "");
        //}
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void recyclerViewItemClicked(View v, final int position) {
        if (v.getId() == R.id.ivTripDeleteIcon) {
            Log.d("Selected Item", locationObjList.get(position).getDate());
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Do you really want to delete trip?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dbHelper.deleteByDate(locationObjList.get(position).getDate());
                            locationObjList.remove(position);
                            tripListAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Trip deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            i.putExtra("selectedDate", locationObjList.get(position).getDate());
            startActivity(i);
        }
    }
}
