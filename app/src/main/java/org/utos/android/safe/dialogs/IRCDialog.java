package org.utos.android.safe.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import org.utos.android.safe.R;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.MODE_PRIVATE;
import static org.utos.android.safe.BaseActivity.CALL_PHONE_PERMISSION;
import static org.utos.android.safe.BaseActivity.CASE_WORKER;
import static org.utos.android.safe.BaseActivity.SHARED_PREFS;

/**
 * Created by zachariah.davis on 2/12/17.
 */
public class IRCDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_irc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setTitle(getString(R.string.irc_all));
        //
        TextView textViewCaseworker = (TextView) layoutInflater.findViewById(R.id.caseWorker);
        TextView textViewNav = (TextView) layoutInflater.findViewById(R.id.ircAddress);
        TextView textViewNumber = (TextView) layoutInflater.findViewById(R.id.ircNumber);
        // set images according to RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            //Right To Left layout
            textViewCaseworker.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_person, 0);
            textViewNav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_navigation, 0);
            textViewNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_call_color_primary, 0);
        } else {
            //Left To Right layout
            textViewCaseworker.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0);
            textViewNav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_navigation, 0, 0, 0);
            textViewNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_color_primary, 0, 0, 0);
        }
        //
        textViewCaseworker.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER, ""));
        //
        textViewNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=221 400 W, Salt Lake City, UT 84110"));
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude()));
                startActivity(intent);
            }
        });
        textViewNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for CALL_PHONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), CALL_PHONE)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).
                                    setTitle("Call Permission").
                                    setMessage("This app needs call permissions to make phone calls.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                        }
                    } else {
                        Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:8013281091"));
                        startActivity(call_intent);
                    }
                } else {
                    Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:8013281091"));
                    startActivity(call_intent);
                }
            }
        });

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