package org.utos.android.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.utos.android.safe.util.GetCaseWorkers;

import java.util.ArrayList;
import java.util.HashMap;


public class SetupActivity extends AppCompatActivity {

    // these strings are keys to access the caseworker and user language values in SharedPrefs
    public static String CASE_WORKER = "caseWorker";
    public static String CASE_WORKER_NUM = "caseWorkerNum";
    public static String USER_NAME = "userName";
    public static String USER_NUMBER = "userNumber";
    public static String USER_LANG = "userLang";
    public static String SHARED_PREFS = "sharedPrefsFile";

    private Spinner mCaseWorkerSpinner;
    private Spinner mLanguageSpinner;
    private SharedPreferences mPrefs;
    //    private String[] mCaseworkerArray;
    //    private String[] mLanguageArray;
    private TextInputEditText textInputEditTextName, textInputEditTextNum;

    private Animation shake;

    private String stringCaseWorkerName, stringCaseWorkerNum;

    private ArrayList<HashMap<String, String>> formCaseWorkerList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Shared Preferences
        mPrefs = getSharedPreferences(SHARED_PREFS, 0);

        // if all data is already collected move to login screen
        if (mPrefs.contains(CASE_WORKER) && mPrefs.contains(CASE_WORKER_NUM) && mPrefs.contains(USER_NAME) && mPrefs.contains(USER_NUMBER) && mPrefs.contains(USER_LANG)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //UI
        textInputEditTextName = (TextInputEditText) findViewById(R.id.input_name);
        textInputEditTextNum = (TextInputEditText) findViewById(R.id.input_num);

        // holding case worker info JSON
        formCaseWorkerList = new GetCaseWorkers(this).getCaseWorkers();

        // animation for feedback
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        // change title
        setTitle(getString(R.string.title_activity_setup));

        //
        if (!mPrefs.contains(CASE_WORKER) || !mPrefs.contains(USER_LANG)) {
            pullData();
        }

        // setup caseworker spinner
        mCaseWorkerSpinner = (Spinner) findViewById(R.id.spinner_caseworker);
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

        // setup language spinner
        mLanguageSpinner = (Spinner) findViewById(R.id.spinner_user_lang);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(languageAdapter);

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
    public void continueClicked(View view) {
        // make sure name is filled
        if (textInputEditTextName.getText().toString().equals("")) {
            // feedback
            textInputEditTextName.setError(getString(R.string.setup_required));
            textInputEditTextName.startAnimation(shake);
        }
        // make sure number is filled
        if (!isValidPhoneNumber(textInputEditTextNum.getText().toString())) {
            textInputEditTextNum.setError(getString(R.string.setup_not_valid));
            textInputEditTextNum.startAnimation(shake);
        }

        // make sure all fields are filled b4 sending
        if (!textInputEditTextName.getText().toString().equals("") && !textInputEditTextNum.getText().toString().equals("")) {
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
}
