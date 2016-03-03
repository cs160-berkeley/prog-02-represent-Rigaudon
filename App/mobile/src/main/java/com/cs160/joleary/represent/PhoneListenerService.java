package com.cs160.joleary.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
private static final String SHAKE = "/shake";
    private static final String DETAIL = "/detail";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(SHAKE) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, CongressionalActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Zip", value);
            startActivity(intent);

        }else if(messageEvent.getPath().equalsIgnoreCase(DETAIL)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, DetailedActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Congressman", value);
            startActivity(intent);
        }else {
            super.onMessageReceived( messageEvent );
        }

    }
}
