package com.badboy.gpstracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.badboy.gpstracker.db.DBHelper;
import com.badboy.gpstracker.model.LocationObj;
import com.badboy.gpstracker.utils.Constants;

public class GPSService extends Service {
    private static final String CHANNEL_DEFAULT_IMPORTANCE = "GPS_Tracker";
    private LocationListener locationListener;
    private LocationManager locationManager;
    DBHelper dbHelper;
    private long time;

    public GPSService() {
        dbHelper = new DBHelper(this);
    }

    public static boolean IS_SERVICE_RUNNING = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Notification notification =
                null;
        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        notificationIntent.setAction("Notification"); // it indicate that user is redirected from notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // intent for action button for stopping service (self)
        Intent stopSelf = new Intent(this, GPSService.class);
        //stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            getNotificationManager().createNotificationChannel(MyApp.getMapScanningChannel());
        }
        notification = getNotification(pendingIntent, pStopSelf);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);




       /*
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("GPS Tracker")
                .setTicker("GPS Tracker")
                .setContentText("Location tracking is in process ...")
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);*/

        return START_STICKY;
    }

    private Notification getNotification(PendingIntent pOpenTrip, PendingIntent pStopSelf) {
        Notification stickyNotification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder nBuilder = new Notification.Builder(this, MyApp.MAP_SCANNING_SERVICE)
                    .setContentTitle("GPS Tracing")
                    .setContentText("GPS Tracing")
                    .setStyle(new Notification.BigTextStyle().bigText("GPS Tracing"))
                    .setSmallIcon(R.drawable.ic_launcher)
                    /* .addAction(R.drawable.ic_outline_close_24px, "EXIT", pStopSelf)*/
                    .setShowWhen(true)
                    .setWhen(time)
                    .setOngoing(true)
                    .setContentIntent(pOpenTrip);
            stickyNotification = nBuilder.build();

        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("GPS Tracing")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("GPS Tracing"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_launcher)
                    /*.addAction(R.drawable.ic_outline_close_24px, "EXIT", pStopSelf)*/
                    .setContentIntent(pOpenTrip)
                    .setShowWhen(true)
                    .setWhen(time)
                    .setOngoing(true);
            stickyNotification = mBuilder.build();
        }
        return stickyNotification;
    }


    private NotificationManager mNotificationManager;

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
    @Override
    public void onCreate() {
        time = System.currentTimeMillis();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    LocationObj locationObj = new LocationObj();
                    locationObj.setLatitude(location.getLatitude() + "");
                    locationObj.setLongitude(location.getLongitude() + "");
                    locationObj.setSpeed(location.getSpeed() + "");
                    locationObj.setAccuracy(location.getAccuracy() + "");
                    dbHelper.insertLocationData(locationObj);
                    Toast.makeText(getApplicationContext(), location.getLatitude() + " - " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }


        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 100, locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(locationListener);
        }
    }
}
