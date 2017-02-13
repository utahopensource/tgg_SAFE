package org.utos.android.safe;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.utos.android.safe.dialogs.AttachImageDialog;
import org.utos.android.safe.dialogs.HospitalDialog;
import org.utos.android.safe.dialogs.IRCDialog;
import org.utos.android.safe.gps.GPSStarterKit;
import org.utos.android.safe.updater.UpdateChecker;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private String calling;
    ////// Localization //////
    public GPSStarterKit gpsStarterKit;
    ////// Localization //////

    //TODO: Authentication

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));
//        toolbar.setLogo(R.drawable.ic_action_hands);

        // set title works when language change
        setTitle(getString(R.string.app_name));

        // Check for Location, Call, and Storage Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE) || ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Location, Call, and Storage Permission").
                            setMessage("This app needs location permission to get current location for reports, call permission to make phone calls, and storage permission to get images and videos.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION, CALL_PHONE, WRITE_EXTERNAL_STORAGE}, CALL_AND_LOCATION_AND_WRITE_PERMISSIONS);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CALL_PHONE, WRITE_EXTERNAL_STORAGE}, CALL_AND_LOCATION_AND_WRITE_PERMISSIONS);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "Settings");
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.action_hospital:
                Log.d(TAG, "Hospital");
                new HospitalDialog().show(getSupportFragmentManager(), "dialog");
                return true;
            case R.id.action_irc:
                Log.d(TAG, "IRC Info");
                new IRCDialog().show(getSupportFragmentManager(), "dialog");
                return true;
            default:
                Log.d(TAG, "default");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop using GPS
        if (gpsStarterKit != null) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop using GPS
        if (gpsStarterKit != null) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop using GPS
        if (gpsStarterKit != null) {
            gpsStarterKit.stopUsingGPS();
        }
    }

    /**
     * Will make call to caseworker
     */
    public void callCaseworker(View view) {
        // Check for CALL_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
            calling="caseworker";
            //
            if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this).
                            setTitle("Call Permission").
                            setMessage("This app needs call permissions to make phone calls.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                }
            } else {
                Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
                startActivity(call_intent);
            }
        } else {
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
            startActivity(call_intent);
        }

    }

    /**
     * Will make call to 911
     */
    public void startUrgent(View view) {
        // Check for CALL_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
            calling="urgent";
            //
            if (ActivityCompat.checkSelfPermission(MainActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Call Permission").
                            setMessage("This app needs call permissions to make phone calls.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
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
                // TODO: 2/12/2017 send sms
            }
        } else {
            // TODO: 1/30/17 need to change to 911
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
            startActivity(call_intent);
            // TODO: 2/12/2017 send sms
        }

        // TODO Need SEND_SMS Permissions send text to case worker
        //        SmsManager smsManager = SmsManager.getDefault();
        //        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
    }

    /**
     * Send SMS
     */
    public void sendSMS(String phoneNumber, String message) {
        //        Intent sendIntent = new Intent(ACTION_SMS_SENT);
//        sendIntent.putExtra("extra_key", "extra_value");
////
        PendingIntent piSent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SMS_SENT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SMS_DELIVERED), 0);
////
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();
////
        SmsManager smsManager = SmsManager.getDefault();
////
        int length = message.length();
        if (length > MAX_SMS_MESSAGE_LENGTH) {
            ArrayList<String> messageList = smsManager.divideMessage(message);
            ////
            for (int i = 0; i < messageList.size(); i++) {
                sentPendingIntents.add(i, piSent);
                deliveredPendingIntents.add(i, piDelivered);
            }
            /////
            Log.d(TAG, "GREATER");
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageList, sentPendingIntents, deliveredPendingIntents);
        } else {
            Log.d(TAG, "LESS");
            smsManager.sendTextMessage(phoneNumber, null, message, piSent, piDelivered);
        }
    }


//    public void sendText(View view) {
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_NO_CREATE);
//        deliverActivity = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_LONG).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_LONG).show();
//                        break;
//
//                }
//            }
//        };
//
//
//        // http://stackoverflow.com/questions/6361428/how-can-i-send-sms-messages-in-the-background-using-android
//        SmsManager smsManager = SmsManager.getDefault();
//
//        smsManager.sendTextMessage("phone number goes here", null, "Message goes here.", null, null);
//    }

    public void shareLocation(double lat, double lng) {
        String string = "This is my current location. \n http://maps.google.com/maps?q=loc:" + lat + "," + lng;
        //
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is my current location");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, string);
        startActivity(Intent.createChooser(sharingIntent, "Share Location"));
    }

    public String buildLocationURL(double lat, double lng) {

        return "http://maps.google.com/maps?q=loc:" + lat + "," + lng;
    }

    /**
     * b
     * Open non urgent activity
     */
    public void startNonUrgent(View view) {
        Intent intent = new Intent(this, NonUrgentActivity.class);
        startActivity(intent);
    }

    /**
     * /**Start GPS
     */
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
    // Permission Listener Results
    ////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_AND_LOCATION_AND_WRITE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // start gps
                    startGPS();
                } else {
                    // Permission Denied
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // check for app update
                    new UpdateChecker(this);
                } else {
                    // Permission Denied
                }
                break;
            case CALL_PHONE_PERMISSION:
                if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    switch (calling){
                        case "urgent":
                            // TODO: 1/30/17 need to change to 911
                            Intent callUrgent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.test_number)));
                            startActivity(callUrgent);
                            // TODO: 2/12/2017 send sms
                            break;
                        case "caseworker":
                            Intent callCaseworker = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
                            startActivity(callCaseworker);
                            break;
                    }
                } else {
                    // Permission Denied
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////
    // Permission Listener Results
    ////////////////////////////////////////////////////////

}
