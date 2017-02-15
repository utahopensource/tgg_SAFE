package org.utos.android.safe.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.utos.android.safe.R;
import org.utos.android.safe.app.AppController;
import org.utos.android.safe.model.PlacesModel;
import org.utos.android.safe.util.ActivePhoneCall;

import java.util.ArrayList;

import static org.utos.android.safe.app.AppController.TAG;

/**
 * Created by zachariah.davis on 2/12/17.
 */
public class PlacesOptionsDialog extends DialogFragment {

    private ArrayList<PlacesModel> list;
    private int pos;
    private Context ctx;
    private ProgressDialog mProgressDialog;
    private JsonObjectRequest jsonObjReq;

    public PlacesOptionsDialog newInstance(int _pos, ArrayList<PlacesModel> placesList) {
        PlacesOptionsDialog f = new PlacesOptionsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", _pos);
        bundle.putParcelableArrayList("placesList", placesList);
        f.setArguments(bundle);

        return f;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pos = getArguments().getInt("pos");
        list = getArguments().getParcelableArrayList("placesList");

        mProgressDialog = new ProgressDialog(ctx);
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        ctx = context;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems((getActivity().getResources().getStringArray(R.array.places_array)), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Call
                        if (!new ActivePhoneCall().isCallActive((getActivity()))) {
                            // uri to access phone number
                            Uri uri = new Uri.Builder().scheme("https").
                                    authority("maps.googleapis.com").
                                    path("maps/api/place/details/json").
                                    appendQueryParameter("placeid", list.get(pos).getPlaceID()).
                                    appendQueryParameter("key", "AIzaSyAxgXsIFTM2mO6mccBCv7IXCxnKhbL325s").
                                    build();
                            Log.d("url phone onClick", uri.toString());
                            showProgressDialog(getActivity(), getActivity().getString(R.string.loading), false);
                            //
                            jsonObjReq = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {
                                @Override public void onResponse(JSONObject response) {
                                    //                    Log.d(TAG, response.toString());
                                    try {
                                        final JSONObject resultPage = response.getJSONObject("result");
                                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        // Phone Number
                                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        if (!resultPage.isNull("formatted_phone_number")) {
                                            try {
                                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                callIntent.setData(Uri.parse("tel:" + resultPage.getString("formatted_phone_number")));
                                                ctx.startActivity(callIntent);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "No Phone Number Listed.", Toast.LENGTH_LONG).show();
                                        }

                                        // hide the progress dialog
                                        hideProgressDialog();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "An error has occurred. Check back again later.", Toast.LENGTH_SHORT).show();
                                    }
                                    // hide the progress dialog
                                    hideProgressDialog();
                                }
                            }, new Response.ErrorListener() {

                                @Override public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("VolleyError", "Error: " + error.getMessage());
                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    // hide the progress dialog
                                    hideProgressDialog();
                                }
                            });

                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(jsonObjReq);
                        }
                        break;
                    case 1:
                        // Nav
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + list.get(pos).getLat() + "," + list.get(pos).getLng()));
                        intent.setPackage("com.google.android.apps.maps");
                        getActivity().startActivity(intent);
                        break;
                    default:
                        // User cancelled, do nothing
                        break;
                }

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        if (getDialog() != null && getDialog().isShowing()) {
            if (jsonObjReq != null) {
                jsonObjReq.cancel();
            }
            getDialog().dismiss();
        }
    }

    @Override public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (getDialog() != null && getDialog().isShowing()) {
            if (jsonObjReq != null) {
                jsonObjReq.cancel();
            }
            getDialog().dismiss();
        }
    }

    @Override public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.d(TAG, "onDismiss");
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (getDialog() != null && getDialog().isShowing()) {
            if (jsonObjReq != null) {
                jsonObjReq.cancel();
            }
            getDialog().dismiss();
        }
    }

    private void showProgressDialog(Context ctx, String message, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ctx);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.setMessage(message);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}