package org.utos.android.safe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.utos.android.safe.gps.GPSStarterKit;
import org.utos.android.safe.gps.GetLocationInfo;
import org.utos.android.safe.updater.UpdateChecker;

import java.util.List;

import static org.utos.android.safe.SetupActivity.CASE_WORKER;
import static org.utos.android.safe.SetupActivity.SHARED_PREFS;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION = 101;
    private static final int WRITE_EXTERNAL_STORAGE = 102;
    private static final int CALL_PHONE = 103;
    ////// Localization //////
    private GPSStarterKit gpsStarterKit;
    ////// Localization //////
    public TextView textViewMyCurrentAddress, textViewCaseWorker;
    //TODO: Authentication

    //TODO:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check for app update
        new UpdateChecker(this);

        // UI stuff
        textViewMyCurrentAddress = (TextView) findViewById(R.id.textViewMyCurrentAddress);
        textViewCaseWorker = (TextView) findViewById(R.id.textViewCaseWorker);

        // set case worker name from shared prefs
        textViewCaseWorker.setText("- " + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER, ""));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for Location Permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).
                        setTitle("Location Permission").
                        setMessage("This app needs location permissions.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
        } else {
            startGPS();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    public void startUrgentCall(View view) {
        // Check for CALL_PHONE
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).
                        setTitle("Call Permission").
                        setMessage("This app needs call permissions to make phone calls.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
            }
        } else {
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
            startActivity(call_intent);
        }

        // TODO Need SEND_SMS Permissions
        //        SmsManager smsManager = SmsManager.getDefault();
        //        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
    }

    public void startUrgentText(View view) {
        // TODO Need SEND_SMS Permissions
        // http://stackoverflow.com/questions/6361428/how-can-i-send-sms-messages-in-the-background-using-android
        //        SmsManager smsManager = SmsManager.getDefault();
        //        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
    }

    public void shareLocation(View view) {
        String string = "This is my current location. \n http://maps.google.com/maps?q=loc:" + gpsStarterKit.getLatitude() + "," + gpsStarterKit.getLongitude();
        //
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is my current location");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, string);
        startActivity(Intent.createChooser(sharingIntent, "Share Location"));
    }

    public void startNonUrgent(View view) {
        Intent intent = new Intent(this, NonUrgentActivity.class);
        startActivity(intent);
    }

    private void startGPS() {
        //
        gpsStarterKit = new GPSStarterKit(this);
        gpsStarterKit.getLocation();
        //
        if (!gpsStarterKit.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsStarterKit.showSettingsAlert();
        }

        // set location in main activity
        List<Address> addresses = new GetLocationInfo(this, gpsStarterKit.getLatitude(), gpsStarterKit.getLongitude()).getLocInfo();
        textViewMyCurrentAddress.setText(addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());
    }

    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startGPS();
                } else {
                    // Permission Denied
                }
                break;
            case WRITE_EXTERNAL_STORAGE:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new UpdateChecker(this);
                } else {
                    // Permission Denied
                }
                break;
            case CALL_PHONE:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////

}
