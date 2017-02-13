package org.utos.android.safe.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.utos.android.safe.MainActivity;
import org.utos.android.safe.R;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.MODE_PRIVATE;
import static org.utos.android.safe.BaseActivity.CALL_PHONE_PERMISSION;
import static org.utos.android.safe.BaseActivity.CASE_WORKER;
import static org.utos.android.safe.BaseActivity.SHARED_PREFS;
import static org.utos.android.safe.BaseActivity.USER_LANG_LOCALE;

/**
 * Created by zachariah.davis on 2/12/17.
 */
public class HospitalDialog extends DialogFragment {

    String stringKey = "AIzaSyAxgXsIFTM2mO6mccBCv7IXCxnKhbL325s";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
//                "?location=" + ((MainActivity) getActivity()).gpsStarterKit.getLatitude() + "," + ((MainActivity) getActivity()).gpsStarterKit.getLongitude() +
//                "&rankby=distance" +
//                "&language="+ getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "en") +
//                "&type=hospital" +
//                "&key=" + stringKey;
//        Log.d("HospitalDialog",urlString);
//        Uri.Builder uriBuilder = new Uri.Builder();
//        builder.scheme("https")
//                .authority("www.myawesomesite.com")
//                .appendPath("turtles")
//                .appendPath("types")
//                .appendQueryParameter("type", "1")
//                .appendQueryParameter("sort", "relevance")
//                .fragment("section-name");
//        String myUrl = builder.build().toString();


        final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_hospital, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setTitle(getString(R.string.hospital));
        //
        builder.setPositiveButton(getString(R.string.pref_profile_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}