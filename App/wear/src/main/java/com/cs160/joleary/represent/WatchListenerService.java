package com.cs160.joleary.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Intent intent = new Intent(this, MainActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //you need to add this flag since you're starting a new activity from a service
        intent.putExtra("loc", Integer.parseInt(value));
        intent.putExtra("curr",0);
        startActivity(intent);

    }
}