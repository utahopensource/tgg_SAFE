package org.utos.android.safe.dialogs.camTEST.dep;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.utos.android.safe.NonUrgentActivity;
import org.utos.android.safe.R;

import java.io.File;
import java.io.IOException;


/**
 * Created by zachariah.davis on 1/24/17.
 */
@SuppressWarnings("deprecation") public class AttachDepVideoDialog extends DialogFragment implements View.OnClickListener {

    // https://inducesmile.com/android/android-camera2-api-example-tutorial/
    private final String TAG = "AttachDepVideoDialog";
    private Camera mCamera;
    private CameraPreviewDep mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture;
    private FrameLayout cameraPreview;
    private boolean isRecording = false;
    private boolean flashOn = false;
    private String mNextVideoAbsolutePath;
    private AppCompatImageButton toggleButtonFlash;
    private Camera.Parameters params;
    private Context ctx;

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_video_dep, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(inflate);

        // don't let user cancel dialog
        setCancelable(false);

        //////
        // Create an instance of Camera
        mCamera = getCameraInstance();

        setCameraDisplayOrientation(mCamera);

        //set camera to continually auto-focus
        params = mCamera.getParameters();
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(params);

        cameraPreview = (FrameLayout) inflate.findViewById(R.id.camera_preview);

        mPreview = new CameraPreviewDep(getActivity(), mCamera);
        cameraPreview.addView(mPreview);

        //
        capture = (Button) inflate.findViewById(R.id.button_capture);
        AppCompatImageButton closeBtn = (AppCompatImageButton) inflate.findViewById(R.id.closeCam);
        toggleButtonFlash = (AppCompatImageButton) inflate.findViewById(R.id.toggleFlash);

        // on clicks
        capture.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        toggleButtonFlash.setOnClickListener(this);
        //////

        // Create the AlertDialog object and return it
        return builder.create();
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        ctx = context;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera()) {
            Toast toast = Toast.makeText(ctx, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            getDialog().dismiss();
        }
        // remove flash btn if doesn't exist
        if (!hasFlash()) {
            toggleButtonFlash.setVisibility(View.INVISIBLE);
        }
        if (mCamera == null) {
            new Thread(new Runnable() {
                public void run() {
                    mCamera = Camera.open();
                }
            }).start();
        }
    }

    @Override public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        if (isRecording) {
            // delete current video f recording
            deleteCurrentVideo();
        }
        releaseMediaRecorder();
        releaseCamera();
        if (getDialog() != null) {
            getDialog().dismiss();
        }

    }

    private boolean hasCamera() {
        // check if the device has camera
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean hasFlash() {
        // check if the device has camera
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder() {
        // realise b4 grabbing instance
        releaseCamera();
        // get instance
        mCamera = getCameraInstance();

        int orientation = setCameraDisplayOrientation(mCamera);
        Log.d(TAG, "" + orientation);

        //set camera to continually auto-focus
        params = mCamera.getParameters();
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        //
        if (flashOn) {
            Log.d(TAG,"flashOn");
            try {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                flashOn = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCamera.setParameters(params);

        //
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        //        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mediaRecorder.setOrientationHint(orientation);

        // Step 4: Set output file
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }
        // External location
        File mediaStorageDir = new File(((NonUrgentActivity) ctx).REPORT_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(((NonUrgentActivity) ctx).REPORT_DIRECTORY_NAME, "Oops! Failed create " + ((NonUrgentActivity) ctx).REPORT_DIRECTORY_NAME + " directory");
            }
        }
        mediaRecorder.setOutputFile(mNextVideoAbsolutePath);

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private static Camera getCameraInstance() {
        Camera c;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
        } catch (Exception e) {
            c = null;
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private int setCameraDisplayOrientation(Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        result = (info.orientation - degrees + 360) % 360;

        camera.setDisplayOrientation(result);

        return result;
    }

    private String getVideoFilePath() {

        //        File finalDestImage = new File(storageDir.getPath() + File.separator + "video_" + (videoArray.size() + 1) + ".mp4");

        return ((NonUrgentActivity) ctx).REPORT_DIRECTORY_NAME + File.separator + "video_" + (((NonUrgentActivity) getActivity()).videoArray.size() + 1) + ".mp4";
    }


    //////////////////////////////////////
    // On Clicks
    //////////////////////////////////////
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_capture:
                if (isRecording) {
                    isRecording = false;
                    // stop recording and release camera
                    mediaRecorder.stop();  // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder
                    Toast.makeText(ctx, "Video captured!", Toast.LENGTH_LONG).show();
                    ///
                    //                    releaseMediaRecorder();
                    releaseCamera();
                    getDialog().dismiss();

                    // add to array
                    ((NonUrgentActivity) getActivity()).videoArray.add(mNextVideoAbsolutePath);
                    // set text
                    ((NonUrgentActivity) getActivity()).attachVideoButton.setText(String.valueOf(((NonUrgentActivity) getActivity()).videoArray.size()));

                } else {
                    isRecording = true;
                    //
                    if (prepareVideoRecorder()) {
                        // initialize video camera
                        new Thread(new Runnable() {
                            public void run() {

                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                try {
                                    mediaRecorder.start();
                                } catch (final Exception ex) {
                                    Log.i("---", "Exception in thread");
                                }
                                //
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Do something on UiThread
                                        // inform the user that recording has started
                                        isRecording = true;
                                        //
                                        capture.setText("Stop");
                                    }
                                });

                            }
                        }).start();
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        // inform user
                    }


                }
                break;
            case R.id.closeCam:
                // close camera dialog
                if (isRecording) {
                    // delete current video f recording
                    deleteCurrentVideo();
                }
                releaseMediaRecorder();
                releaseCamera();
                getDialog().dismiss();
                break;
            case R.id.toggleFlash:
                if (!flashOn) {
                    //
                    try {
                        toggleButtonFlash.setImageResource(R.drawable.ic_flash_on);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(params);
                        flashOn = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        toggleButtonFlash.setImageResource(R.drawable.ic_flash_off);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                        flashOn = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    private void deleteCurrentVideo() {
        if (!mNextVideoAbsolutePath.equals("") || mNextVideoAbsolutePath != null) {
            File file = new File(mNextVideoAbsolutePath);
            boolean deleted = file.delete();
        }
    }
}