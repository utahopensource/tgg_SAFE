package org.utos.android.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SetupActivity extends AppCompatActivity {

    // these strings are keys to access the caseworker and user language values in SharedPrefs
    public static String CASE_WORKER = "caseWorker";
    public static String USER_LANG = "userLang";
    public static String SHARED_PREFS = "sharedPrefsFile";

    private Spinner mCaseWorkerSpinner;
    private Spinner mLanguageSpinner;
    private SharedPreferences mPrefs;
    private String[] mCaseworkerArray;
    private String[] mLanguageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
         mPrefs = getSharedPreferences(SHARED_PREFS, 0);

        if( !mPrefs.contains(CASE_WORKER) || !mPrefs.contains(USER_LANG)){
            pullData ();
        }

        mCaseWorkerSpinner = (Spinner) findViewById(R.id.spinner_caseworker);
        ArrayAdapter<CharSequence> caseWorkerAdapter = ArrayAdapter.createFromResource(this,
                R.array.caseworker_array, android.R.layout.simple_spinner_item);

        caseWorkerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCaseWorkerSpinner.setAdapter(caseWorkerAdapter);

        mLanguageSpinner = (Spinner) findViewById(R.id.spinner_user_lang);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);

        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageSpinner.setAdapter(languageAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void pullData() {
        //TODO: get the data from the DB and populate the value arrays
        
    }

    /**
     * When continue button is clicked the selected case worker and language are stored in Shared Preferences
     * and the Login activity is launched
     * @param view - button clicked in content_setup.xml
     */
    public void continueClicked(View view) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(CASE_WORKER, mCaseWorkerSpinner.getSelectedItem().toString());
        prefsEditor.putString(USER_LANG, mLanguageSpinner.getSelectedItem().toString());
        prefsEditor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
