package org.utos.android.safe;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.utos.android.safe.wrapper.LanguageWrapper;

/**
 * Created by zacdavis on 2/7/17.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String CASE_WORKER = "caseWorker";
    public static final String CASE_WORKER_NUM = "caseWorkerNum";
    public static final String USER_NAME = "userName";
    public static final String USER_NUMBER = "userNumber";
    public static final String USER_LANG = "userLang";
    public static final String USER_LANG_LOCALE = "userLangLocale";
    public static final String SHARED_PREFS = "SharedPrefsFile";

    ///////////////////
    // set language
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageWrapper.wrap(newBase, newBase.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "en")));
    }
    //
    ///////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
