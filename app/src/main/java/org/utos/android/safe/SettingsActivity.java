package org.utos.android.safe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.utos.android.safe.util.localjson.GetCaseWorkers;
import org.utos.android.safe.util.localjson.LanguagesWorkers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // up Nav
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFC800'>Settings</font>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFC800'>Settings</font>"));
        }
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            /////////////////////////////
            // set header app info
            PreferenceCategory appHeadApp = (PreferenceCategory) findPreference("prefs_cat_app_info");
            appHeadApp.setTitle(getString(R.string.cat_app_info));
            // set header user content
            PreferenceCategory appHeadUser = (PreferenceCategory) findPreference("prefs_cat_user_content");
            appHeadUser.setTitle(getString(R.string.cat_user_content));
            /////////////////////////////

            /////////////////////////////
            // set version
            Preference versionPref = findPreference("app_version");
            versionPref.setTitle(getString(R.string.pref_build));
            // get version
            try {
                versionPref.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            /////////////////////////////
            // app licensing
            // TODO: 2/15/17 add any third party licensing into the html doc in asset folder.. Delete this if we dont use any
            Preference appLicensingPref = findPreference("app_licensing");
            appLicensingPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity()).setTitle("Licenses");
                    WebView wv = new WebView(getActivity());
                    // load from asset folder
                    try {
                        InputStream inputStream = getActivity().getResources().getAssets().open("open_source_licenses.html");
                        byte[] b = new byte[inputStream.available()];
                        inputStream.read(b);
                        wv.loadData(new String(b), "text/html", "utf-8");
                    } catch (IOException e) {
                        Log.e(TAG, "Couldn't open upgrade-alert.html", e);
                    }
                    alert.setView(wv);
                    alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    return false;
                }
            });

            /////////////////////////////
            // language
            ArrayList<HashMap<String, String>> fromLanguageList = new LanguagesWorkers(getActivity()).getLanguages();
            final ArrayList<String> stringLang = new ArrayList<>();
            final ArrayList<String> stringLocale = new ArrayList<>();
            for (HashMap<String, String> row : fromLanguageList) {
                stringLang.add(row.get("language"));
                stringLocale.add(row.get("locale"));
            }
            final String[] arrayLang = stringLang.toArray(new String[stringLang.size()]);
            final String[] arrayLocale = stringLocale.toArray(new String[stringLocale.size()]);
            //
            final Preference languagePref = findPreference("app_lang");
            languagePref.setTitle(getString(R.string.pref_language));
            languagePref.setSummary(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG, ""));
            //
            languagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(arrayLang, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (!arrayLang[item].equals(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG, ""))) {
                                // set language preference
                                SharedPreferences.Editor prefsEditor = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                                prefsEditor.putString(USER_LANG, arrayLang[item]);
                                prefsEditor.putString(USER_LANG_LOCALE, arrayLocale[item]);
                                prefsEditor.apply();
                                // reset summary
                                languagePref.setSummary(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG, ""));
                                // refresh
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                    return true;
                }
            });

            /////////////////////////////
            // profile
            Preference profilePref = findPreference("app_profile");
            profilePref.setTitle(getString(R.string.pref_profile));
            profilePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    //
                    final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile, null);
                    // dialog UI
                    //                    CustomCircleNetworkImageView editTextStreetAddress = (CustomCircleNetworkImageView) layoutInflater.findViewById(R.id.profileIMG);
                    TextView textViewGName = (TextView) layoutInflater.findViewById(R.id.googleName);
                    TextView textViewGEmail = (TextView) layoutInflater.findViewById(R.id.googleEmail);
                    TextView textViewUserName = (TextView) layoutInflater.findViewById(R.id.userName);
                    TextView textViewUserNum = (TextView) layoutInflater.findViewById(R.id.userNum);
                    TextView textViewUserLang = (TextView) layoutInflater.findViewById(R.id.userLanguage);
                    TextView textViewUserCW = (TextView) layoutInflater.findViewById(R.id.userCaseWorker);
                    TextView textViewUserCWN = (TextView) layoutInflater.findViewById(R.id.userCaseWorkerNUmber);

                    // set text in UI
                    //                    editTextStreetAddress.setImageUrl(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LoginActivity.LOGIN_PHOTO, ""), VolleySingleton.getInstance().getImageLoader());
                    textViewGName.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LOGIN_NAME, ""));
                    textViewGEmail.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LOGIN_EMAIL, ""));
                    textViewUserName.setText(String.format(getActivity().getString(R.string.user_name), getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NAME, "")));
                    textViewUserNum.setText(String.format(getActivity().getString(R.string.user_num), getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NUMBER, "")));
                    textViewUserLang.setText(String.format(getActivity().getString(R.string.user_lang), getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG, "")));
                    textViewUserCW.setText(String.format(getActivity().getString(R.string.user_caseworker), getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER, "")));
                    textViewUserCWN.setText(String.format(getActivity().getString(R.string.user_caseworker_num), getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER_NUM, "")));

                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setCancelable(false);
                    builder.setPositiveButton(getString(R.string.pref_profile_edit), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int id) {
                            //
                            dialog.dismiss();
                            //
                            final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_edit, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setCancelable(false).setTitle(getString(R.string.pref_profile_edit));

                            //
                            final EditText inputEditTextName = (EditText) layoutInflater.findViewById(R.id.input_name);
                            inputEditTextName.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NAME, ""));

                            //
                            final EditText inputEditTextNum = (EditText) layoutInflater.findViewById(R.id.input_num);
                            inputEditTextNum.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NUMBER, ""));

                            // setup caseworker spinner
                            final Spinner spinner = (Spinner) layoutInflater.findViewById(R.id.spinner_caseworker);
                            final ArrayList<HashMap<String, String>> formCaseWorkerList = new GetCaseWorkers(getActivity()).getCaseWorkers();
                            SimpleAdapter adapterCaseWorker = new SimpleAdapter(getActivity(), formCaseWorkerList, android.R.layout.simple_spinner_dropdown_item, new String[]{"name"}, new int[]{android.R.id.text1});
                            spinner.setAdapter(adapterCaseWorker);
                            //
                            for (HashMap list : formCaseWorkerList) {
                                if (list.get("name").equals(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER, ""))) {
                                    spinner.setSelection(formCaseWorkerList.indexOf(list));
                                }
                            }
                            //
                            builder.setPositiveButton(getString(R.string.pref_profile_save), new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor prefsEditor = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                                    prefsEditor.putString(CASE_WORKER, formCaseWorkerList.get(spinner.getSelectedItemPosition()).get("name"));
                                    prefsEditor.putString(CASE_WORKER_NUM, formCaseWorkerList.get(spinner.getSelectedItemPosition()).get("phone"));
                                    prefsEditor.putString(USER_NAME, inputEditTextName.getText().toString().trim());
                                    prefsEditor.putString(USER_NUMBER, inputEditTextNum.getText().toString().trim());
                                    prefsEditor.apply();
                                }
                            }).setNegativeButton(getString(R.string.pref_profile_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            //
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }).setNegativeButton(getString(R.string.pref_profile_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    //
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return false;
                }
            });

        }

    }

}
