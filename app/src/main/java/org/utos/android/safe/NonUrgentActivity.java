package org.utos.android.safe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NonUrgentActivity extends AppCompatActivity {
    private static final int IMAGE_SELECTION_REQUEST_CODE = 1;
    private static final int VOICE_SELECTION_REQUEST_CODE = 2;
    private static final int VIDEO_SELECTION_REQUEST_CODE = 3;

    private ImageButton attachImageButton;
    private ImageButton attachVoiceButton;
    private ImageButton makePhoneCallButton;
    private ImageButton attachVideoButton;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_urgent);

        String[] arraySpinner = new String[] {
                "1", "2", "3", "4", "5"
        };
        Spinner s = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        attachImageButton = (ImageButton) findViewById(R.id.attachImaage);
        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachImage(view);
            }
        });

        attachVoiceButton = (ImageButton) findViewById(R.id.attachVoice);
        attachVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachVoice(view);
            }
        });

        makePhoneCallButton = (ImageButton) findViewById(R.id.startPhoneCall);
        makePhoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NonUrgentActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    //TODO: grab the current caseworker's phone number
                    intent.setData(Uri.parse("tel:5558675309"));
                    startActivity(intent);
                }
            }
        });

        attachVideoButton = (ImageButton) findViewById(R.id.attachVideo);
        attachVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachVideo(view);
            }
        });

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReport(view);
            }
        });
    }

    public void attachImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECTION_REQUEST_CODE);
        }
    }

    public void attachVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_SELECTION_REQUEST_CODE);
        }
    }

    public void attachVoice(View view) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Recording"), VOICE_SELECTION_REQUEST_CODE);
        }
    }

    public void submitReport(View view) {
        //gather data into object
        //submit data to POST api call to submit the report
        Snackbar snackbar = Snackbar.make(view, "Incident Report submitted", Snackbar.LENGTH_LONG);
        snackbar.show();

        Intent i = new Intent(NonUrgentActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        if (requestCode == IMAGE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("TEST", String.valueOf(bitmap));
                //TODO: what do we do with the data returned
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == VOICE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                String imageUri = uri.getPath();
                File file = new File(imageUri);

                Log.d("TEST", String.valueOf(file));
                //TODO: what do we do with the data returned
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == VIDEO_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                //TODO: at this point we can either use the stream to send data or save to a file to send

                Log.d("TEST", String.valueOf(stream));
                //TODO: what do we do with the data returned
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
