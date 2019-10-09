package com.badboy.gpstracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badboy.gpstracker.db.DBHelper;
import com.badboy.gpstracker.model.LocationObj;
import com.badboy.gpstracker.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private LatLngBounds bounds;
    Intent intent;
    String selectedDate = "";
    TextView tvDistance, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dbHelper = new DBHelper(this);
        intent = getIntent();
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        selectedDate = intent.getStringExtra("selectedDate");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<LocationObj> locationObjs = new ArrayList<LocationObj>();
        List<LatLng> latLngs = new ArrayList<LatLng>();
        locationObjs = dbHelper.getAllData(selectedDate);
        Location startLocation = new Location("A");
        Location endLocation = new Location("B");
        float totalDistance = 0, totalDistanceKm = 0;
        if (locationObjs != null && locationObjs.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < locationObjs.size(); i++) {

                    endLocation = new Location("B");

                    endLocation.setLatitude(Double.parseDouble(locationObjs.get(i).getLatitude()));
                    endLocation.setLongitude(Double.parseDouble(locationObjs.get(i).getLongitude()));
                    if (i > 0) {
                        totalDistance = totalDistance + startLocation.distanceTo(endLocation);
                    }
                    startLocation = new Location("A");
                    startLocation = endLocation;

                    latLngs.add(new LatLng(Double.parseDouble(locationObjs.get(i).getLatitude()), Double.parseDouble(locationObjs.get(i).getLongitude())));
                    builder.include(new LatLng(Double.parseDouble(locationObjs.get(i).getLatitude()), Double.parseDouble(locationObjs.get(i).getLongitude())));

            }
            Marker startPoint = mMap.addMarker(new MarkerOptions()
                    .title("Start")
                    .position(new LatLng(Double.parseDouble(locationObjs.get(0).getLatitude()),Double.parseDouble(locationObjs.get(0).getLongitude())))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            Marker endPoint = mMap.addMarker(new MarkerOptions()
                    .title("End")
                    .position(new LatLng(Double.parseDouble(locationObjs.get(locationObjs.size()-1).getLatitude()),Double.parseDouble(locationObjs.get(locationObjs.size()-1).getLongitude())))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            Log.d("TotalDistance", totalDistance + "");
            totalDistanceKm = totalDistance / 1000;
            tvDistance.setText(Utils.decimalTruncate(totalDistanceKm) + " Km");
            try {
                if (!selectedDate.isEmpty()) {
                    tvDate.setText(Utils.getFormattedDate(selectedDate));
                } else {
                    tvDate.setText("Till now");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bounds = builder.build();
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .width(10)
                    .color(Color.RED));
            line.setPoints(latLngs);


            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                }
            });
        } else {
            Toast.makeText(this, "No data points to plot!!", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //captureScreenshot();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            captureScreenshot();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Bitmap bitmapMap, bitmapSummery, bitmapShare;

    private void captureScreenshot() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {


            @Override
            public void onSnapshotReady(Bitmap snapshot) {

                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                OutputStream output;
                ;
                try {
                    bitmapMap = snapshot;
                    bitmapSummery = takeScreenshot();
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/GPSTracker";
                    boolean var = false;


                    File imageFile = new File(mPath);
                    if (!imageFile.exists())
                        var = imageFile.mkdir();

                    File imageFile1 = new File(imageFile.toString() + "/" + now + ".jpg");
                    Bitmap b = combineImages(bitmapSummery,bitmapMap);
                    FileOutputStream outputStream = new FileOutputStream(imageFile1);
                    int quality = 100;
                    b.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    // Share Intent
                    Intent share = new Intent(Intent.ACTION_SEND);

                    // Type of file to share
                    share.setType("image/jpeg");

                    // Locate the image to Share
                    Uri uri = Uri.fromFile(imageFile1);

                    // Pass the image into an Intnet
                    share.putExtra(Intent.EXTRA_STREAM, uri);

                    // Show the social share chooser list
                    startActivity(Intent.createChooser(share, "Share Image Tutorial"));

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        mMap.snapshot(callback);
    }

    private Bitmap takeScreenshot() {
        View linearLayout;
        linearLayout = findViewById(R.id.llSummery);
        linearLayout.setDrawingCacheEnabled(true);
        linearLayout.buildDrawingCache();
        Bitmap layoutBitmap = Bitmap.createBitmap(linearLayout.getDrawingCache());
        linearLayout.setDrawingCacheEnabled(false);
        return layoutBitmap;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }
}
