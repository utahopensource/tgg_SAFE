package org.utos.android.safe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.utos.android.safe.adapters.RecyclerViewLanguages;
import org.utos.android.safe.model.LanguageModel;
import org.utos.android.safe.util.localjson.LanguagesWorkers;

import java.util.ArrayList;

public class SelectLanguageActivity extends BaseActivity {

    ////////////
    private ArrayList<LanguageModel> languageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout progressBar;
    ////////////

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));

        // set title works when language change
        setTitle(getString(R.string.app_name));

        // goto next if got language
        if (getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).contains(USER_LANG)) {
            Intent intent = new Intent(SelectLanguageActivity.this, SetupActivity.class);
            startActivity(intent);
        }

        ////////////////////////////////////////////////
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        languageList = new LanguagesWorkers(this).getLanguagesLanguageModel();
        mAdapter = new RecyclerViewLanguages(this, languageList);
        mRecyclerView.setAdapter(mAdapter);
        ////////////////////////////////////////////////

        //        ListView listView = (ListView) findViewById(R.id.langList);
        //
        //
        //        SimpleAdapter adapterLanguage = new SimpleAdapter(this, fromLanguageList, android.R.layout.simple_spinner_dropdown_item, new String[]{"language"}, new int[]{android.R.id.text1});
        //        listView.setAdapter(adapterLanguage);
        //
        //        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                SharedPreferences.Editor prefsEditor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        //                prefsEditor.putString(USER_LANG, fromLanguageList.get(position).get("language"));
        //                prefsEditor.putString(USER_LANG_LOCALE, fromLanguageList.get(position).get("locale"));
        //                prefsEditor.apply();
        //
        //                //
        //                Intent intent = new Intent(SelectLanguageActivity.this, SetupActivity.class);
        //                startActivity(intent);
        //            }
        //        });

    }

}
