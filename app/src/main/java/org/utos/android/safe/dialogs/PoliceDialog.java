package org.utos.android.safe.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.utos.android.safe.MainActivity;
import org.utos.android.safe.R;
import org.utos.android.safe.adapters.RecyclerViewAdapterEmpty;
import org.utos.android.safe.adapters.RecyclerViewAdapterPlaces;
import org.utos.android.safe.app.AppController;
import org.utos.android.safe.model.PlacesModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static org.utos.android.safe.BaseActivity.SHARED_PREFS;
import static org.utos.android.safe.BaseActivity.USER_LANG_LOCALE;

/**
 * Created by zachariah.davis on 2/12/17.
 */
public class PoliceDialog extends DialogFragment {

    private String TAG = "HospitalDialog";
    private String stringKey = "AIzaSyAxgXsIFTM2mO6mccBCv7IXCxnKhbL325s";

    private CountDownTimer countDownTimer;
    private JsonObjectRequest jsonObjReq;
    private Uri uri;

    ////////////
    private final ArrayList<PlacesModel> policeList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout progressBar;
    ////////////

    private ProgressDialog progressDialog;

    private boolean gettingMore;
    private boolean countDownRunning;

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View layoutInflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_hospital, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(layoutInflater).setTitle(getString(R.string.police));
        ////////////////////////////////////////////////
        mRecyclerView = (RecyclerView) layoutInflater.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapterPlaces(getActivity(), policeList);
        mRecyclerView.setAdapter(mAdapter);
        ////////////////////////////////////////////////
        getResults(((MainActivity) getActivity()).gpsStarterKit.getLatitude(), ((MainActivity) getActivity()).gpsStarterKit.getLongitude(), "");
        ////////////////////////////////////////////////
        //
        builder.setPositiveButton(getString(R.string.pref_profile_cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
        if (policeList.size() == 0) {
            progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true);
        }
    }

    private String getURL(final double lat, final double lng, final String pageToken) {
        if (pageToken.isEmpty()) {
            // first page
            uri = new Uri.Builder().scheme("https").authority("maps.googleapis.com").
                    path("maps/api/place/nearbysearch/json").
                    appendQueryParameter("location", lat + "," + lng).
                    appendQueryParameter("rankby", "distance").
                    appendQueryParameter("language", getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "en")).
                    appendQueryParameter("types", "police").
                    appendQueryParameter("key", stringKey).
                    build();
        } else {
            // getting extra pages
            uri = new Uri.Builder().scheme("https").authority("maps.googleapis.com").
                    path("maps/api/place/nearbysearch/json").
                    appendQueryParameter("location", lat + "," + lng).
                    appendQueryParameter("rankby", "distance").
                    appendQueryParameter("language", getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(USER_LANG_LOCALE, "en")).
                    appendQueryParameter("types", "police").
                    appendQueryParameter("key", stringKey).
                    appendQueryParameter("pagetoken", pageToken).
                    build();
        }

        Log.d(TAG, uri.toString());

        return uri.toString();
    }

    private String getMiles(double lat, double lng) {
        Location myLoc = new Location("");
        myLoc.setLatitude(((MainActivity) getActivity()).gpsStarterKit.getLatitude());
        myLoc.setLongitude(((MainActivity) getActivity()).gpsStarterKit.getLongitude());
        Location loc2 = new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(lng);
        DecimalFormat df = new DecimalFormat("0.0");
        float distanceInMeters = myLoc.distanceTo(loc2);

        return String.valueOf((df.format(distanceInMeters / 1609.344)));
    }

    private void getResults(final double lat, final double lng, String page) {
        jsonObjReq = new JsonObjectRequest(Request.Method.GET, getURL(lat, lng, page), null, new Response.Listener<JSONObject>() {
            @Override public void onResponse(final JSONObject response) {
                try {
                    JSONArray resultsPage1 = response.getJSONArray("results");
                    // check if there is more pages
                    if (!response.isNull("next_page_token")) {
                        Log.d(TAG, "next_page_token GOT MORE");
                        gettingMore = true;
                        // need countdown to wait for API to refresh
                        countDownTimer = new CountDownTimer(2000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                countDownRunning = true;
                            }

                            public void onFinish() {
                                countDownRunning = false;
                                try {
                                    getResults(lat, lng, response.getString("next_page_token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        Log.d(TAG, "next_page_token NO MORE");
                        gettingMore = false;
                    }
                    Log.d(TAG, "gettingMore " + gettingMore);
                    //
                    for (int i = 0; i < resultsPage1.length(); i++) {
                        //
                        JSONObject result = resultsPage1.getJSONObject(i);
                        //
                        PlacesModel placesModel = new PlacesModel();

                        // name
                        placesModel.setName(result.getString("name"));

                        // set rating
                        if (!result.isNull("rating")) {
                            placesModel.setRating(result.getDouble("rating"));
                        }

                        // set miles from current location
                        placesModel.setMiles(getMiles(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"), result.getJSONObject("geometry").getJSONObject("location").getDouble("lng")));

                        // get place id
                        placesModel.setPlaceID(result.getString("place_id"));

                        // set lat
                        placesModel.setLat(String.valueOf(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat")));

                        // set lng
                        placesModel.setLng(String.valueOf(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng")));

                        // add model to list
                        policeList.add(placesModel);

                    }

                    if (policeList.size() == 0) {
                        //                            Toast.makeText(TaxiActivity.this, "No places found, Search Again.", Toast.LENGTH_SHORT).show();
                        mAdapter = new RecyclerViewAdapterEmpty(getActivity(), "no_results");
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter = new RecyclerViewAdapterPlaces(getActivity(), policeList);
                        mRecyclerView.setAdapter(mAdapter);
                        ////////////////
                        mAdapter.notifyDataSetChanged();
                    }
                    //
                    if (!gettingMore) {
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    //
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
                //
                mAdapter = new RecyclerViewAdapterEmpty(getActivity(), "error");
                mRecyclerView.setAdapter(mAdapter);
                //
                progressDialog.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        //        if (getDialog() != null && getDialog().isShowing()) {
        //            if (countDownRunning) {
        //                countDownTimer.cancel();
        //            }
        //            jsonObjReq.cancel();
        //            //            getDialog().dismiss();
        //        }
    }

    @Override public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
        //        if (progressDialog.isShowing()) {
        //            progressDialog.dismiss();
        //        }
        //
        //        if (getDialog() != null && getDialog().isShowing()) {
        //            if (countDownRunning) {
        //                countDownTimer.cancel();
        //            }
        //            jsonObjReq.cancel();
        //            //            getDialog().dismiss();
        //        }
    }

    @Override public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.d(TAG, "onDismiss");
        //        if (progressDialog.isShowing()) {
        //            progressDialog.dismiss();
        //        }
        //
        //        if (getDialog() != null && getDialog().isShowing()) {
        //            if (countDownRunning) {
        //                countDownTimer.cancel();
        //            }
        //            jsonObjReq.cancel();
        //            //            getDialog().dismiss();
        //        }
    }

}