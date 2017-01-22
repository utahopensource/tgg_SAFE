package org.utos.android.safe.updater;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import org.utos.android.safe.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zac on 1/22/2017.
 */
public class UpdateChecker {
    // TODO need to find a hosting place for these files.  I currently use Firebase.

    /**
     * JSON response url
     */
    private final String urlJson = "https://firebasestorage.googleapis.com/v0/b/snoop-4dcd4.appspot.com/o/data%20(3).json?alt=media&token=0a65525d-21e9-4509-9170-230439b606f0";

    private final String TAG = "UpdateChecker";
    private final Activity ctx;
    //    private ProgressDialog loadingDialog;
    private ProgressDialog mProgressDialog;
    private static final int WRITE_EXTERNAL_STORAGE = 102;
    private final int myVersionCode;

    public UpdateChecker(Activity _ctx) {
        ctx = _ctx;
        //
        myVersionCode = BuildConfig.VERSION_CODE;

        // Check for WRITE_EXTERNAL_STORAGE
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx).
                        setTitle("Write External Storage Permission").
                        setMessage("This app needs rite External Storage Permission to update app.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            }
        } else {
            checkJSON();
        }
    }

    /**
     * Method to make json object request
     */
    private void checkJSON() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJson, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                //
                try {
                    // Parsing json object response
                    // response will be a json object
                    /**
                     * {
                     "currentVersion":2,
                     "apkUrl":"https://firebasestorage.googleapis.com/v0/b/snoop-4dcd4.appspot.com/o/app-debug.apk?alt=media&token=2067d5b2-008c-4687-83ce-707369793da6"
                     }
                     */
                    int jsonVersionCode = response.getInt("currentVersion");
                    Log.d("jsonVersionCode", "" + jsonVersionCode);

                    // APK download URL
                    final String apkUrl = response.getString("apkUrl");
                    Log.d("apkUrl", "" + apkUrl);

                    //
                    if (jsonVersionCode > myVersionCode) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx).
                                setMessage("Click \"YES\" to download new update.").
                                setTitle("Update Available").
                                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Download new APK
                                        mProgressDialog = new ProgressDialog(ctx);
                                        mProgressDialog.setMessage("Downloading Update");
                                        mProgressDialog.setIndeterminate(true);
                                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        mProgressDialog.setCancelable(false);

                                        // execute this when the downloader must be fired
                                        final DownloadTask downloadTask = new DownloadTask(ctx);
                                        downloadTask.execute(apkUrl);

                                        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                downloadTask.cancel(true);
                                            }
                                        });

                                    }
                                }).
                                setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private final Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/safe.apk");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else {
                // Download successful start install
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/safe.apk");
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri apkURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                install.setDataAndType(apkURI, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(install);
            }
        }
    }
}