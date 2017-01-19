package org.utos.android.safe.gps;

/**
 * Created by zachariah.davis on 12/6/16.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

public class GPSStarterKit implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute

    // Declaring a Location Manager
    private LocationManager locationManager;
    //
    private CountDownTimer countDownTimer;

    public GPSStarterKit(Context context) {
        this.mContext = context;
        //        getLocation();
    }

    public Location getLocation() {
        ////////////////////
        // only get location for 30 seconds then stop
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                //                Log.d("onTick", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                stopUsingGPS();
            }
        }.start();
        ////////////////////
        Log.d("getLocation", "getLocation");
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(mContext, "No network provider is enabled.", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
                // Get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        // stop countdown timer
        countDownTimer.cancel();
        //
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
                locationManager = null;
                Log.d("stopUsingGPS", "stopUsingGPS");
            }

        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    ////////////////////////////////////////////////////////
    // LocationListener
    ////////////////////////////////////////////////////////
    @Override public void onLocationChanged(Location location) {
        //        Log.d("onLocationChanged", "onLocationChanged - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude());
        //
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override public void onProviderDisabled(String provider) {
        //        Log.d("onProviderDisabled", "onProviderDisabled");
    }

    @Override public void onProviderEnabled(String provider) {
        //        Log.d("onProviderEnabled", "onProviderEnabled");
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {
        //        Log.d("onStatusChanged", "onStatusChanged");
    }
    ////////////////////////////////////////////////////////
    // LocationListener
    ////////////////////////////////////////////////////////

    //    @Override public IBinder onBind(Intent arg0) {
    //        return null;
    //    }

}
