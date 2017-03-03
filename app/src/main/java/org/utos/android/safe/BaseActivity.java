package org.utos.android.safe;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.utos.android.safe.wrapper.LanguageWrapper;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by zacdavis on 2/7/17.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    // Shared Preferences
    public static final String SHARED_PREFS = "SharedPrefsFile";
    public static final String CASE_WORKER = "caseWorker";
    public static final String CASE_WORKER_NUM = "caseWorkerNum";
    public static final String USER_NAME = "userName";
    public static final String USER_NUMBER = "userNumber";
    public static final String USER_LANG = "userLang";
    public static final String USER_LANG_LOCALE = "userLangLocale";
    public static final String LOGIN_NAME = "loginName";
    public static final String LOGIN_PHOTO = "loginPhoto";
    public static final String LOGIN_EMAIL = "loginEmail";
    public static final String LOGIN_UNIQUE_ID = "uniqueUserId";
    public static final String LOGIN_OAUTH2 = "loginOauth2";
    // Shared Preferences

    // Permissions
    public static final int ALL_PERMISSION = 101;
    public static final int CALL_AND_LOCATION_AND_WRITE_PERMISSIONS = 102;
    public static final int CALL_PHONE_PERMISSION = 103;
    public static final int CAM_AND_WRITE_EXTERNAL_STORAGE_PERMISSION = 104;
    public static final int RECORD_AUDIO_WRITE_EXTERNAL_STORAGE_PERMISSION = 105;
    public final String[] PERMISSIONS = {CAMERA, ACCESS_FINE_LOCATION, RECORD_AUDIO, CALL_PHONE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE};
    // Permissions

    // SMS Receivers
    public static String ACTION_SMS_SENT = "SMS_SENT";
    public static String ACTION_SMS_DELIVERED = "SMS_DELIVERED";
    public static int MAX_SMS_MESSAGE_LENGTH = 160;
    // SMS Receivers

    private ProgressDialog mProgressDialog;

    ///////////////////
    // set language
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageWrapper.wrap(newBase, newBase.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "en")));
    }
    //
    ///////////////////

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "" + getCurrentLocale());
    }

    //
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
    //    @TargetApi(Build.VERSION_CODES.N) public Locale getCurrentLocale() {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    //            return getResources().getConfiguration().getLocales().get(0);
    //        } else {
    //            //noinspection deprecation
    //            return getResources().getConfiguration().locale;
    //        }
    //    }

    //    public void showProgressDialog(Context ctx, String message, boolean cancelable) {
    //        if (mProgressDialog == null) {
    //            mProgressDialog = new ProgressDialog(ctx);
    //            mProgressDialog.setCancelable(cancelable);
    //            mProgressDialog.setMessage(message);
    //            mProgressDialog.setIndeterminate(true);
    //        }
    //
    //        mProgressDialog.show();
    //    }
    //
    //    public void hideProgressDialog() {
    //        if (mProgressDialog != null && mProgressDialog.isShowing()) {
    //            mProgressDialog.dismiss();
    //        }
    //    }
}
