package org.utos.android.safe.util;

import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import org.utos.android.safe.R;

/**
 * Created by zachariah.davis on 2/14/17.
 */
public class ActivePhoneCall {

    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager.getMode() == AudioManager.MODE_IN_CALL) {
            Toast.makeText(context, context.getString(R.string.inCall), Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }
}
