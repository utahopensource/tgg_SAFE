package org.utos.android.safe.util.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Android on 2/11/2017.
 */

public class DeliveryBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED";

    // When the SMS has been delivered
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_SMS_DELIVERED)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS Delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
