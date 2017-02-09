package org.utos.android.safe.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import org.utos.android.safe.NonUrgentActivity;

import java.io.File;
import java.io.IOException;

import static org.utos.android.safe.NonUrgentActivity.REPORT_DIRECTORY_NAME;

/**
 * Created by zachariah.davis on 1/24/17.
 */
public class AttachAudioDialog {

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

    public void recordAudio(final Activity ctx) {
        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            final MediaRecorder mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + REPORT_DIRECTORY_NAME + File.separator + "audio_" + (((NonUrgentActivity) ctx).audioArray.size() + 1) + ".mp3";
            mediaRecorder.setOutputFile(path);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();

                final ProgressDialog mProgressDialog = new ProgressDialog(ctx);
                mProgressDialog.setTitle("Audio Recording");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Start Talking...");
                mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop recording", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mProgressDialog.dismiss();
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        // Save a path
                        ((NonUrgentActivity) ctx).audioArray.add(path);
                        Log.d(((NonUrgentActivity) ctx).TAG, "" + path);
                        // set text
                        ((NonUrgentActivity) ctx).attachVoiceButton.setText(String.valueOf(((NonUrgentActivity) ctx).audioArray.size()));
                    }
                });
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface p1) {
                        mediaRecorder.stop();
                        mediaRecorder.release();
                    }
                });
                mProgressDialog.show();
            } catch (IllegalStateException | IOException e) {

                e.printStackTrace();

            }

        }

    }

}