package org.utos.android.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.utos.android.safe.dialogs.AttachAudioDialog;
import org.utos.android.safe.dialogs.AttachImageDialog;
import org.utos.android.safe.dialogs.AttachVideoDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.utos.android.safe.SetupActivity.CASE_WORKER_NUM;
import static org.utos.android.safe.SetupActivity.SHARED_PREFS;

public class NonUrgentActivity extends AppCompatActivity {

    private String TAG = "NonUrgentActivity";

    // Permissions
    private static final int CALL_PHONE = 101;
    private static final int CAM_AND_WRITE_EXTERNAL_STORAGE = 102;
    private static final int RECORD_AUDIO_WRITE_EXTERNAL_STORAGE = 103;

    // Activity request codes
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static final int VOICE_SELECTION_REQUEST_CODE = 300;
    public static final int SELECT_VIDEO_SELECTION_REQUEST_CODE = 400;
    public static final int CAPTURE_VIDEO_SELECTION_REQUEST_CODE = 500;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    public static final String REPORT_DIRECTORY_NAME = "SAFE" + File.separator + "Report";

    public ImageButton attachImageButton, attachVoiceButton, attachVideoButton;
    private Spinner spinner;
    private EditText editTextDesc;

    public String mCurrentImagePath, mCurrentAudioPath, mCurrentVideoPath, whatToDo;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_urgent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));

        // setup up nav
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // UI
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        spinner = (Spinner) findViewById(R.id.spinnerCat);
        attachImageButton = (ImageButton) findViewById(R.id.attachImage);
        attachVoiceButton = (ImageButton) findViewById(R.id.attachVoice);
        attachVideoButton = (ImageButton) findViewById(R.id.attachVideo);

    }

    /**
     * returning image / video
     */
    public File getOutputMediaFile(int type) throws IOException {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + REPORT_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(REPORT_DIRECTORY_NAME, "Oops! Failed create " + REPORT_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            Log.d("MEDIA_TYPE_IMAGE", "MEDIA_TYPE_IMAGE");
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg");
            // Save a path
            mCurrentImagePath = mediaFile.getAbsolutePath();
        } else if (type == MEDIA_TYPE_VIDEO) {
            Log.d("MEDIA_TYPE_VIDEO", "MEDIA_TYPE_VIDEO");
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "video.mp4");
            // Save a path
            mCurrentVideoPath = mediaFile.getAbsolutePath();
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Will make call to caseworker
     */
    public void makeCall(View view) {
        // Check for CALL_PHONE
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).
                        setTitle("Call Permission").
                        setMessage("This app needs call permissions to make phone calls.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int which) {
                        ActivityCompat.requestPermissions(NonUrgentActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PHONE);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PHONE);
            }
        } else {
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
            startActivity(call_intent);
        }

    }

    /**
     * Will launch camera app or select imgae
     */
    public void attachImage(View view) {
        if (isGooglePlayServicesAvailable(this)) {
            //
            whatToDo = "attachImage";
            //
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Camera and Write to Storage Permission").
                            setMessage("This app needs permissions to access camera and write to storage.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(NonUrgentActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_AND_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_AND_WRITE_EXTERNAL_STORAGE);
                }
            } else {
                new AttachImageDialog().show(getSupportFragmentManager(), "dialog");
            }
        }

    }

    /**
     * Will launch camera app request video capture
     */
    public void attachVideo(View view) {
        if (isGooglePlayServicesAvailable(this)) {
            //
            whatToDo = "attachVideo";
            //
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Camera and Write to Storage Permission").
                            setMessage("This app needs permissions to access camera and write to storage.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(NonUrgentActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_AND_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_AND_WRITE_EXTERNAL_STORAGE);
                }
            } else {
                new AttachVideoDialog().show(getSupportFragmentManager(), "dialog");
            }
        }

    }

    /**
     * Will launch audio recorder
     */
    public void attachVoice(View view) {
        if (isGooglePlayServicesAvailable(this)) {
            //
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).
                            setTitle("Record Audio and Write to Storage Permission").
                            setMessage("This app needs permissions to record audio and write to storage.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int which) {
                            ActivityCompat.requestPermissions(NonUrgentActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO_WRITE_EXTERNAL_STORAGE);
                }
            } else {
                new AttachAudioDialog().recordAudio(this);
            }
        }
    }

    private void deleteImage() {
        File file = new File(mCurrentImagePath);
        boolean deleted = file.delete();
    }

    private void deleteAudio() {
        File file = new File(mCurrentAudioPath);
        boolean deleted = file.delete();
    }

    private void deleteVideo() {
        File file = new File(mCurrentVideoPath);
        boolean deleted = file.delete();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  CAMERA_CAPTURE_IMAGE_REQUEST_CODE
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("RESULT_OK", "RESULT_OK");
                if (new File(mCurrentImagePath).exists()) {
                    Log.d("RESULT_OK", "exists");
                    attachImageButton.setColorFilter(Color.parseColor("#009900"));
                    attachImageButton.setImageResource(R.drawable.ic_check);
                } else {
                    Log.d("RESULT_OK", "NO exists");
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                // delete file
                deleteImage();
                // return images to norm
                attachImageButton.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                attachImageButton.setImageResource(R.drawable.ic_add_photo);
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                // delete file
                deleteImage();
                // return images to norm
                attachImageButton.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                attachImageButton.setImageResource(R.drawable.ic_add_photo);
            }
        }

        // SELECT_IMAGE_ACTIVITY_REQUEST_CODE
        if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        BufferedInputStream reader = new BufferedInputStream(inputStream);

                        // Create an output stream to a file that you want to save to
                        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + REPORT_DIRECTORY_NAME);

                        // Create the storage directory if it does not exist
                        if (!storageDir.exists()) {
                            if (!storageDir.mkdirs()) {
                                Toast.makeText(getApplicationContext(), "Unable to create directory! Please verify there is space available on the device.", Toast.LENGTH_LONG).show();
                            }
                        }

                        File finalDestImage = new File(storageDir.getPath() + File.separator + "image.jpg");
                        Log.d("destinationImage", String.valueOf(finalDestImage));
                        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(finalDestImage));

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = reader.read(buf)) > 0) {
                            outStream.write(buf, 0, len);

                        }
                        inputStream.close();
                        outStream.close();

                        // Save a path
                        mCurrentImagePath = finalDestImage.getAbsolutePath();
                        //
                        Log.d("mCurrentImagePath", "B4");
                        if (new File(mCurrentImagePath).exists()) {
                            attachImageButton.setColorFilter(Color.parseColor("#009900"));
                            attachImageButton.setImageResource(R.drawable.ic_check);
                        }
                    } catch (IOException re) {
                        re.printStackTrace();
                        Toast.makeText(getApplicationContext(), "File not found, try another image.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "File not found, try another image.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image selection", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to select image", Toast.LENGTH_SHORT).show();
            }

        }

        //  CAPTURE_VIDEO_SELECTION_REQUEST_CODE
        if (requestCode == CAPTURE_VIDEO_SELECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (new File(mCurrentVideoPath).exists()) {
                    attachVideoButton.setColorFilter(Color.parseColor("#009900"));
                    attachVideoButton.setImageResource(R.drawable.ic_check);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
                // delete file
                deleteVideo();
                // return images to norm
                attachVideoButton.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                attachVideoButton.setImageResource(R.drawable.ic_video);
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                // delete file
                deleteVideo();
                // return images to norm
                attachVideoButton.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                attachVideoButton.setImageResource(R.drawable.ic_video);
            }
        }

        //  SELECT_VIDEO_SELECTION_REQUEST_CODE
        if (requestCode == SELECT_VIDEO_SELECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        BufferedInputStream reader = new BufferedInputStream(inputStream);

                        // Create an output stream to a file that you want to save to
                        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + REPORT_DIRECTORY_NAME);

                        // Create the storage directory if it does not exist
                        if (!storageDir.exists()) {
                            if (!storageDir.mkdirs()) {
                                Toast.makeText(getApplicationContext(), "Unable to create directory! Please verify there is space available on the device.", Toast.LENGTH_LONG).show();
                            }
                        }

                        File finalDestImage = new File(storageDir.getPath() + File.separator + "video.mp4");
                        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(finalDestImage));

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = reader.read(buf)) > 0) {
                            outStream.write(buf, 0, len);

                        }
                        inputStream.close();
                        outStream.close();

                        // Save a path
                        mCurrentVideoPath = finalDestImage.getAbsolutePath();
                        //
                        if (new File(mCurrentVideoPath).exists()) {
                            attachVideoButton.setColorFilter(Color.parseColor("#009900"));
                            attachVideoButton.setImageResource(R.drawable.ic_check);
                        }
                    } catch (IOException re) {
                        re.printStackTrace();
                        Toast.makeText(getApplicationContext(), "File not found, try another video.", Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "File not found, try another video.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(), "User cancelled video selection", Toast.LENGTH_SHORT).show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(), "Sorry! Failed to select video", Toast.LENGTH_SHORT).show();
            }
        }

        // VOICE_SELECTION_REQUEST_CODE
        if (requestCode == VOICE_SELECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (new File(mCurrentVideoPath).exists()) {
                    attachVideoButton.setColorFilter(Color.parseColor("#009900"));
                    attachVideoButton.setImageResource(R.drawable.ic_check);
                }
            }
        }

    }

    public void submitReport(View view) {
        //gather data into object
        //submit data to POST api call to submit the report

        if (!editTextDesc.getText().toString().equals("")) {
            // Description Text
            String desString = editTextDesc.getText().toString().trim();
            Log.d(TAG + " desSt", desString);

            // Category Selection
            String catSelectionString = spinner.getSelectedItem().toString();
            Log.d(TAG + " catSe", catSelectionString);

            // Image File
            if (mCurrentImagePath != null) {
                File imageFile = new File(mCurrentImagePath);
                if (imageFile.exists()) {
                    Log.d(TAG + " image", imageFile.getAbsolutePath());
                }
            }

            // Video File
            if (mCurrentVideoPath != null) {
                File videoFile = new File(mCurrentVideoPath);
                if (videoFile.exists()) {
                    Log.d(TAG + " video", videoFile.getAbsolutePath());
                }
            }

            // Audio File
            if (mCurrentAudioPath != null) {
                File audioFile = new File(mCurrentAudioPath);
                if (audioFile.exists()) {
                    Log.d(TAG + " audio", audioFile.getAbsolutePath());
                }
            }

            //
            Snackbar snackbar = Snackbar.make(view, "Incident Report submitted", Snackbar.LENGTH_LONG);
            snackbar.show();
            // close activity when done
            //            finish();

            // TODO: 1/30/17 delete all files when done submitting
        } else {
            //
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            editTextDesc.startAnimation(shake);
            editTextDesc.setError(getString(R.string.setup_required));
            //
            Snackbar snackbar = Snackbar.make(view, "Description is needed before submission", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////
    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getSharedPreferences(SHARED_PREFS, 0).getString(CASE_WORKER_NUM, "")));
                    startActivity(call_intent);
                } else {
                    // Permission Denied
                }
                break;
            case CAM_AND_WRITE_EXTERNAL_STORAGE:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (whatToDo != null) {
                        switch (whatToDo) {
                            case "attachImage":
                                new AttachImageDialog().show(getSupportFragmentManager(), "dialog");
                                break;
                            case "attachVideo":
                                new AttachVideoDialog().show(getSupportFragmentManager(), "dialog");
                                break;
                        }
                    }
                } else {
                    // Permission Denied
                }
                break;
            case RECORD_AUDIO_WRITE_EXTERNAL_STORAGE:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new AttachAudioDialog().recordAudio(this);
                } else {
                    // Permission Denied
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////
    // Permission Listener
    ////////////////////////////////////////////////////////

    // check google play services is available and updated
    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            Log.d("PlayServicesAvailable", "false");
            return false;
        }
        Log.d("PlayServicesAvailable", "true");
        return true;
    }

}