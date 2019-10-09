package com.badboy.gpstracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.badboy.gpstracker.db.DBHelper;
import com.badboy.gpstracker.model.LocationObj;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Button button_service;
    DBHelper dbHelper;
    FloatingActionButton actionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);
       // button_service = (Button) findViewById(R.id.button_service);
        actionButton = (FloatingActionButton) findViewById(R.id.button_service_status);
        /*LocationObj locationObj = new LocationObj("1","17.6","73.3","5","109");
        dbHelper.insertLocationData(locationObj);*/


        if (!runtime_permission()) {
            if (GPSService.IS_SERVICE_RUNNING) {
                actionButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24px));
            } else {
                actionButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24px));
            }

            enable_buttons();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_maps, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trips) {
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_plot_all) {
            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
            i.putExtra("selectedDate", "");
            startActivity(i);
        } else if (id == R.id.nav_backup) {
            try {
                exportEmailInCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean runtime_permission() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                enable_buttons();
            } else {
                runtime_permission();
            }
        }
    }

    private void enable_buttons() {


       /* button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GPSService.class);
                GPSService.IS_SERVICE_RUNNING = true;
                button_start.setVisibility(View.INVISIBLE);
                button_stop.setVisibility(View.VISIBLE);
                startService(i);
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GPSService.class);
                GPSService.IS_SERVICE_RUNNING = false;
                button_start.setVisibility(View.VISIBLE);
                button_stop.setVisibility(View.INVISIBLE);
                stopService(i);
            }
        });*/
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), GPSService.class);
                if (!GPSService.IS_SERVICE_RUNNING) {
                    startService(service);
                    GPSService.IS_SERVICE_RUNNING = true;
                    actionButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24px));
                    //button_service.setText("Stop Service");
                } else {
                    stopService(service);
                    GPSService.IS_SERVICE_RUNNING = false;
                    actionButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24px));
                    //button_service.setText("Start Service");

                }
            }
        });



    }

    public void exportEmailInCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/GPSTracker");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            final String filename = Environment.getExternalStorageDirectory() + "/GPSTracker/"+ now + ".csv";
            final File backupFile = new File(filename);
            // show waiting screen
            CharSequence contentTitle = getString(R.string.app_name);
            final ProgressDialog progDailog = ProgressDialog.show(
                    HomeActivity.this, contentTitle, "Backup in progress...",
                    true);//please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {



                    // Share Intent
                    Intent share = new Intent(Intent.ACTION_SEND);

                    // Type of file to share
                    share.setType("*/*");

                    Uri uri = FileProvider.getUriForFile(HomeActivity.this, getApplicationContext().getPackageName() + ".provider",backupFile);
                    // Pass the image into an Intnet
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Person Details");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final PackageManager pm = getPackageManager();
                    final List<ResolveInfo> matches = pm.queryIntentActivities(share, 0);
                    ResolveInfo best = null;
                    for (final ResolveInfo info : matches)
                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                            best = info;
                    if (best != null)
                        share.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    // Show the social share chooser list
                    startActivity(share);
                }


            };

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);
                        List<LocationObj> locationObjList=null;
                        locationObjList = dbHelper.getAllData("");

                        fw.append("Id");
                        fw.append(',');

                        fw.append("Date");
                        fw.append(',');

                        fw.append("Latitude");
                        fw.append(',');

                        fw.append("Longitude");
                        fw.append(',');

                        fw.append("Accuracy");
                        fw.append(',');

                        fw.append("Speed");
                        fw.append(',');

                        fw.append("Created date");
                        fw.append(',');



                        fw.append('\n');

                        for(int z=0;z<locationObjList.size();z++) {

                            fw.append(locationObjList.get(z).getId());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getDate());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getLatitude());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getLongitude());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getAccuracy());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getSpeed());
                            fw.append(',');

                            fw.append(locationObjList.get(z).getCreatedDate());
                            fw.append(',');



                            fw.append('\n');

                        }


                        fw.flush();
                        fw.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();

                }
            }.start();

        }

    }

}
