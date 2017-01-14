package org.utos.android.safe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NonUrgentActivity extends AppCompatActivity {
    private static final int IMAGE_SELECTION_REQUEST_CODE = 1;
    private static final int VIDEO_SELECTION_REQUEST_CODE = 2;
    private static final int VOICE_SELECTION_REQUEST_CODE = 3;

    private ImageButton attachImageButton;
    private ImageButton attachVideoButton;
    private ImageButton attachVoiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_urgent);

        attachImageButton = (ImageButton) findViewById(R.id.attachImaage);
        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachImage(view);
            }
        });

        attachVideoButton = (ImageButton) findViewById(R.id.attachVideo);
        attachVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachVideo(view);
            }
        });

        attachVoiceButton = (ImageButton) findViewById(R.id.attachVoice);
        attachVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachVoice(view);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("TEST", String.valueOf(bitmap));
                //TODO: what do we do with the data returned
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == VIDEO_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                //TODO: at this point we can either use the stream to send data or save to a file to send

                Log.d("TEST", String.valueOf(stream));
                //TODO: what do we do with the data returned
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == VOICE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                String imageUri = uri.getPath();
                File file = new File(imageUri);

                Log.d("TEST", String.valueOf(file));
                //TODO: what do we do with the data returned
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
