package org.utos.android.safe.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.utos.android.safe.NonUrgentActivity;
import org.utos.android.safe.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.utos.android.safe.NonUrgentActivity.CAMERA_CAPTURE_IMAGE_REQUEST_CODE;
import static org.utos.android.safe.NonUrgentActivity.MEDIA_TYPE_IMAGE;
import static org.utos.android.safe.NonUrgentActivity.SELECT_IMAGE_ACTIVITY_REQUEST_CODE;


/**
 * Created by zachariah.davis on 1/24/17.
 */
public class AttachImageDialog extends DialogFragment {

    private Activity mContext;

    public AttachImageDialog() {
        mContext = getActivity();
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(getResources().getStringArray(R.array.get_pic), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Take Photo
                        if (isExternalStorageReadable() && isExternalStorageWritable()) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = ((NonUrgentActivity) getActivity()).getOutputMediaFile(MEDIA_TYPE_IMAGE);
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    Log.i("cameraIntent", "IOException");
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", photoFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    // FileProvider permissions
                                    // https://github.com/commonsguy/cw-omnibus/blob/master/Camera/FileProvider/app/src/main/java/com/commonsware/android/camcon/MainActivity.java
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "A photo", photoURI);

                                        cameraIntent.setClipData(clip);
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    } else {
                                        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);

                                        for (ResolveInfo resolveInfo : resInfoList) {
                                            String packageName = resolveInfo.activityInfo.packageName;
                                            getActivity().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                        }
                                    }
                                    // Verify that the intent will resolve to an activity
                                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        getActivity().startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Device storage is not currently available. Check to see if the device is connected to a computer or if the storage has been removed.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        // Select Photo
                        if (isExternalStorageReadable() && isExternalStorageWritable()) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
                        } else {
                            Toast.makeText(getActivity(), "Device storage is not currently available. Check to see if the device is connected to a computer or if the storage has been removed.", Toast.LENGTH_LONG).show();
                        }
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

}