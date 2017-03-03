package org.utos.android.safe.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.utos.android.safe.MainActivity;
import org.utos.android.safe.R;
import org.utos.android.safe.util.ActivePhoneCall;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.MODE_PRIVATE;
import static org.utos.android.safe.BaseActivity.CALL_PHONE_PERMISSION;
import static org.utos.android.safe.BaseActivity.CASE_WORKER;
import static org.utos.android.safe.BaseActivity.CASE_WORKER_NUM;
import static org.utos.android.safe.BaseActivity.SHARED_PREFS;

/**
 * Created by zachariah.davis on 2/12/17.
 */
public class IRCDialog extends DialogFragment {

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_irc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater);
        //
        TextView textViewCaseworker = (TextView) layoutInflater.findViewById(R.id.caseWorker);
        TextView caseWorkerNum = (TextView) layoutInflater.findViewById(R.id.caseWorkerNum);
        TextView textViewNav = (TextView) layoutInflater.findViewById(R.id.ircAddress);
        TextView textViewNumber = (TextView) layoutInflater.findViewById(R.id.ircNumber);
        // set images according to RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            //Right To Left layout
            textViewCaseworker.setCompoundDrawablesWithIntrinsicBounds(null, null, VectorDrawableCompat.create(getResources(), R.drawable.ic_person, getActivity().getTheme()), null);
            caseWorkerNum.setCompoundDrawablesWithIntrinsicBounds(null, null, VectorDrawableCompat.create(getResources(), R.drawable.ic_call_color_primary, getActivity().getTheme()), null);
            textViewNav.setCompoundDrawablesWithIntrinsicBounds(null, null, VectorDrawableCompat.create(getResources(), R.drawable.ic_navigation, getActivity().getTheme()), null);
            textViewNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, VectorDrawableCompat.create(getResources(), R.drawable.ic_call_color_primary, getActivity().getTheme()), null);

            //
            textViewNav.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            textViewNumber.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else {
            //Left To Right layout
            textViewCaseworker.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_person, getActivity().getTheme()), null, null, null);
            caseWorkerNum.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_call_color_primary, getActivity().getTheme()), null, null, null);
            textViewNav.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_navigation, getActivity().getTheme()), null, null, null);
            textViewNumber.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_call_color_primary, getActivity().getTheme()), null, null, null);

            //
            textViewNav.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textViewNumber.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        }
        //
        textViewCaseworker.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER, ""));
        caseWorkerNum.setText(getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CASE_WORKER_NUM, ""));
        textViewNav.setText(getString(R.string.irc_address));
        textViewNumber.setText(getString(R.string.irc_number));
        //
        caseWorkerNum.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Check for CALL_PHONE
                if (!new ActivePhoneCall().isCallActive(getActivity())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //
                        ((MainActivity) getActivity()).calling = "caseworker";
                        //
                        if (ActivityCompat.checkSelfPermission(getActivity(), CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), CALL_PHONE)) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity()).
                                        setTitle("Call Permission").
                                        setMessage("This app needs call permissions to make phone calls.");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialogInterface, int which) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                                    }
                                });
                                android.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{CALL_PHONE}, CALL_PHONE_PERMISSION);
                            }
                        } else {
                            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getActivity().getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
                            startActivity(call_intent);
                        }
                    } else {
                        Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getActivity().getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
                        startActivity(call_intent);
                    }
                }
            }
        });
        //
        textViewNav.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=221 400 W, Salt Lake City, UT 84110"));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=40.764473, -111.902098"));
                startActivity(intent);
            }
        });
        textViewNumber.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Check for CALL_PHONE
                if (!new ActivePhoneCall().isCallActive(getActivity())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), CALL_PHONE)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).
                                        setTitle("Call Permission").
                                        setMessage("This app needs call permissions to make phone calls.");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialogInterface, int which) {
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
            }
        });

        //
        builder.setPositiveButton(getString(R.string.pref_profile_cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}