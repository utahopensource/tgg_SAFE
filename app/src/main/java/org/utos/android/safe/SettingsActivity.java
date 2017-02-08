package org.utos.android.safe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.utos.android.safe.util.localjson.GetCaseWorkers;
import org.utos.android.safe.util.localjson.LanguagesWorkers;

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
            // set version
            Preference versionPref = findPreference("app_version");
            // get version
            try {
                versionPref.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            /////////////////////////////
            // videos
            //            Preference videosPref = findPreference("app_version");
            //            videosPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            //                @Override public boolean onPreferenceClick(Preference preference) {
            //
            //                    return false;
            //                }
            //            });

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
                    TextView textViewUserLan = (TextView) layoutInflater.findViewById(R.id.userLanguage);
                    TextView textViewUserCW = (TextView) layoutInflater.findViewById(R.id.userCaseWorker);
                    TextView textViewUserCWN = (TextView) layoutInflater.findViewById(R.id.userCaseWorkerNUmber);

                    // set text in UI
                    //                    editTextStreetAddress.setImageUrl(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LoginActivity.LOGIN_PHOTO, ""), VolleySingleton.getInstance().getImageLoader());
                    textViewGName.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LoginActivity.LOGIN_NAME, ""));
                    textViewGEmail.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(LoginActivity.LOGIN_EMAIL, ""));
                    textViewUserName.setText("User Name: " + getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NAME, ""));
                    textViewUserNum.setText("User Number: " + getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NUMBER, ""));
                    textViewUserLan.setText("User Language: " + getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG, ""));
                    textViewUserCW.setText("Case Worker: " + getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER, ""));
                    textViewUserCWN.setText("Case Worker Number: " + getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER_NUM, ""));
                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setCancelable(false).setTitle("Profile");
                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int id) {
                            //
                            dialog.dismiss();
                            //
                            final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile_edit, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setCancelable(false).setTitle("User Profile Edit");

                            //
                            final TextInputEditText inputEditTextName = (TextInputEditText) layoutInflater.findViewById(R.id.input_name);
                            inputEditTextName.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_NAME, ""));

                            //
                            final TextInputEditText inputEditTextNum = (TextInputEditText) layoutInflater.findViewById(R.id.input_num);
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
                            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor prefsEditor = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                                    prefsEditor.putString(CASE_WORKER, formCaseWorkerList.get(spinner.getSelectedItemPosition()).get("name"));
                                    prefsEditor.putString(CASE_WORKER_NUM, formCaseWorkerList.get(spinner.getSelectedItemPosition()).get("phone"));
                                    prefsEditor.putString(USER_NAME, inputEditTextName.getText().toString().trim());
                                    prefsEditor.putString(USER_NUMBER, inputEditTextNum.getText().toString().trim());
                                    prefsEditor.apply();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            //
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
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
