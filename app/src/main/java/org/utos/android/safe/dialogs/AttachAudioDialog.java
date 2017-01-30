package org.utos.android.safe.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Environment;

import org.utos.android.safe.NonUrgentActivity;
import org.utos.android.safe.R;

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
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + REPORT_DIRECTORY_NAME + File.separator + "audio.3gp";
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
                        ((NonUrgentActivity) ctx).mCurrentAudioPath = path;
                        if (new File(((NonUrgentActivity) ctx).mCurrentAudioPath).exists()) {
                            ((NonUrgentActivity) ctx).attachVoiceButton.setColorFilter(Color.parseColor("#009900"));
                            ((NonUrgentActivity) ctx).attachVoiceButton.setImageResource(R.drawable.ic_check);
                        }
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