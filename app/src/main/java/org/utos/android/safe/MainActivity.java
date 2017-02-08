package org.utos.android.safe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.utos.android.safe.gps.GPSStarterKit;
import org.utos.android.safe.updater.UpdateChecker;
import org.utos.android.safe.wrapper.LanguageWrapper;

import static android.Manifest.permission.CALL_PHONE;

public class MainActivity extends BaseActivity {

    //    private static final int LOCATION_PERMISSION = 101;
    private static final int CALL_AND_LOCATION_AND_WRITE_PERMISSIONS = 101;
    //    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 102;
    private static final int CALL_PHONE_PERMISSION = 103;

    ////// Localization //////
    private GPSStarterKit gpsStarterKit;
    ////// Localization //////
    private TextView textViewMyCurrentAddress, textViewCaseWorker;
    private boolean makeCall;
    //TODO: Authentication

    ///////////////////
    // set language
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageWrapper.wrap(newBase, newBase.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "")));
    }
    //
    ///////////////////

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));

        // UI stuff
        textViewMyCurrentAddress = (TextView) findViewById(R.id.textViewMyCurrentAddress);
        textViewCaseWorker = (TextView) findViewById(R.id.textViewCaseWorker);

        // Check for Location, Call, and Storage Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Location, Call, and Storage Permission").
                            setMessage("This app needs location permission to get current location for reports, call permission to make phone calls, and storage permission to get images and videos.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CALL_AND_LOCATION_AND_WRITE_PERMISSIONS);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CALL_AND_LOCATION_AND_WRITE_PERMISSIONS);
                }
            } else {
                // start gps
                startGPS();
                // check for app update
                new UpdateChecker(this);
            }
        } else {
            // start gps
            startGPS();
            // check for app update
            new UpdateChecker(this);
        }

        // set case worker name from shared prefs
        //        textViewCaseWorker.setText("- " + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER, ""));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();

    }

    @Override protected void onStop() {
        super.onStop();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override public void onPause() {
        super.onPause();

        // stop using GPS
        if (gpsStarterKit != (null)) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    public void startUrgent(View view) {
        // Check for CALL_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //
                makeCall = true;
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Call Permission").
                            setMessage("This app needs call permissions to make phone calls.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                }
            } else {
                // TODO: 1/30/17 need to change to 911
                Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
                startActivity(call_intent);
            }
        } else {
            // TODO: 1/30/17 need to change to 911
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
            startActivity(call_intent);
        }

        // TODO Need SEND_SMS Permissions send text to case worker
        //        SmsManager smsManager = SmsManager.getDefault();
        //        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
    }

    //    public void startUrgentText(View view) {
    //        // TODO Need SEND_SMS Permissions
    //        // http://stackoverflow.com/questions/6361428/how-can-i-send-sms-messages-in-the-background-using-android
    //        //        SmsManager smsManager = SmsManager.getDefault();
    //        //        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
    //    }

    //    public void shareLocation(View view) {
    //        String string = "This is my current location. \n http://maps.google.com/maps?q=loc:" + gpsStarterKit.getLatitude() + "," + gpsStarterKit.getLongitude();
    //        //
    //        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    //        sharingIntent.setType("text/plain");
    //        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is my current location");
    //        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, string);
    //        startActivity(Intent.createChooser(sharingIntent, "Share Location"));
    //}

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
        //        List<Address> addresses = new GetLocationInfo(this, gpsStarterKit.getLatitude(), gpsStarterKit.getLongitude()).getLocInfo();
        //        textViewMyCurrentAddress.setText(addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());
    }

    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_AND_LOCATION_AND_WRITE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // start gps
                    startGPS();
                } else {
                    // Permission Denied
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // check for app update
                    new UpdateChecker(this);
                } else {
                    // Permission Denied
                }
                break;
            case CALL_PHONE_PERMISSION:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (makeCall) {
                        // TODO: 1/30/17 need to change to 911
                        Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
                        startActivity(call_intent);
                    }
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
