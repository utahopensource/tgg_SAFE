package org.utos.android.safe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.utos.android.safe.util.GetCaseWorkers;

import java.util.ArrayList;
import java.util.HashMap;


public class SetupActivity extends AppCompatActivity {

    // these strings are keys to access the caseworker and user language values in SharedPrefs
    private static final String CASE_WORKER = "caseWorker";
    public static final String CASE_WORKER_NUM = "caseWorkerNum";
    private static final String USER_NAME = "userName";
    private static final String USER_NUMBER = "userNumber";
    public static final String USER_LANG = "userLang";
    public static final String SHARED_PREFS = "sharedPrefsFile";

    private Spinner mCaseWorkerSpinner;
    private Spinner mLanguageSpinner;
    private SharedPreferences mPrefs;
    //    private String[] mCaseworkerArray;
    //    private String[] mLanguageArray;
    private TextInputEditText textInputEditTextName, textInputEditTextNum;
    TextInputLayout textInputLayoutName, textInputLayoutNum;

    private Animation shake;

    private String stringCaseWorkerName, stringCaseWorkerNum;

    private ArrayList<HashMap<String, String>> formCaseWorkerList;

    // Permissions
    private static final int ALL_PERMISSION = 101;
    private final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));

        // change title
        setTitle(getString(R.string.title_activity_setup));

        //UI
        mCaseWorkerSpinner = (Spinner) findViewById(R.id.spinner_caseworker);
        mLanguageSpinner = (Spinner) findViewById(R.id.spinner_user_lang);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        textInputLayoutNum = (TextInputLayout) findViewById(R.id.input_layout_num);
        textInputEditTextName = (TextInputEditText) findViewById(R.id.input_name);
        textInputEditTextNum = (TextInputEditText) findViewById(R.id.input_num);

        // setup language spinner
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(languageAdapter);

        // Shared Preferences
        mPrefs = getSharedPreferences(SHARED_PREFS, 0);

        // if all data is already collected move to login screen
        if (mPrefs.contains(CASE_WORKER) && mPrefs.contains(CASE_WORKER_NUM) && mPrefs.contains(USER_NAME) && mPrefs.contains(USER_NUMBER) && mPrefs.contains(USER_LANG)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Run Permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSION);
            } else {
                // get phonenumber
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                // if NULL dont set number
                if (tMgr.getLine1Number() != null) {
                    textInputEditTextNum.setText(tMgr.getLine1Number());
                }

                // Open the Spinner
                mLanguageSpinner.performClick();
            }

            // setup animation for feedback
            shake = AnimationUtils.loadAnimation(this, R.anim.shake);

            ////////////////////////////////////////////////////////
            // // TODO: 1/30/17 might want to run everything below in onRequestPermissionsResult after permissions run
            if (!mPrefs.contains(CASE_WORKER) || !mPrefs.contains(USER_LANG)) {
                pullData();
            }

            // setup caseworker spinner
            formCaseWorkerList = new GetCaseWorkers(this).getCaseWorkers();
            SimpleAdapter adapter = new SimpleAdapter(this, formCaseWorkerList, android.R.layout.simple_spinner_dropdown_item, new String[]{"name"}, new int[]{android.R.id.text1});
            mCaseWorkerSpinner.setAdapter(adapter);
            mCaseWorkerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    stringCaseWorkerName = formCaseWorkerList.get(position).get("name");
                    stringCaseWorkerNum = formCaseWorkerList.get(position).get("phone");
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ////////////////////////////////////////////////////////
        }

    }

    private void pullData() {
        //TODO: get the data from the DB and populate the value arrays
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches();
    }

    /**
     * When continue button is clicked the selected case worker and language are stored in Shared Preferences
     * and the Login activity is launched
     *
     * @param view - button clicked in content_setup.xml
     */
    public void continueClicked(@SuppressWarnings("UnusedParameters") View view) {
        // make sure name is filled
        if (textInputEditTextName.getText().toString().equals("")) {
            // feedback
            textInputEditTextName.setError(getString(R.string.setup_required));
            textInputLayoutName.startAnimation(shake);
        }
        // make sure number is filled
        if (!isValidPhoneNumber(textInputEditTextNum.getText().toString())) {
            textInputEditTextNum.setError(getString(R.string.setup_not_valid));
            textInputLayoutNum.startAnimation(shake);
        }

        // make sure all fields are filled b4 sending
        if (!textInputEditTextName.getText().toString().equals("") && !textInputEditTextNum.getText().toString().equals("") && isGooglePlayServicesAvailable(this)) {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString(CASE_WORKER, stringCaseWorkerName);
            prefsEditor.putString(CASE_WORKER_NUM, stringCaseWorkerNum);
            prefsEditor.putString(USER_NAME, textInputEditTextName.getText().toString().trim());
            prefsEditor.putString(USER_NUMBER, textInputEditTextNum.getText().toString().trim());
            prefsEditor.putString(USER_LANG, mLanguageSpinner.getSelectedItem().toString());
            prefsEditor.apply();

            //
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    // check google play services is available and updated
    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            Log.d("PlayServicesAvailable", "false");
            return false;
        }
        Log.d("PlayServicesAvailable", "true");
        return true;
    }

    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSION:
                if (isGooglePlayServicesAvailable(SetupActivity.this)) {
                    // check READ_PHONE_STATE and get phonenumber
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted get phonenumber
                        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        // if NULL dont set number
                        if (tMgr.getLine1Number() != null) {
                            textInputEditTextNum.setText(tMgr.getLine1Number());
                        }
                    }
                    ///////
                    // Open the Spinner
                    mLanguageSpinner.performClick();
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////
}
