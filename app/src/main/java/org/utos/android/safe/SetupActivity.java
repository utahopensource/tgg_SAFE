package org.utos.android.safe;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SetupActivity extends AppCompatActivity {

    private Spinner mCaseWorkerSpinner;
    private Spinner mLanguageSpinner;

//    Spinner spinner = (Spinner) findViewById(R.id.spinner);
//    // Create an ArrayAdapter using the string array and a default spinner layout
//    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//            R.array.planets_array, android.R.layout.simple_spinner_item);
//    // Specify the layout to use when the list of choices appears
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    // Apply the adapter to the spinner
//    spinner.setAdapter(adapter);
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

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

}
