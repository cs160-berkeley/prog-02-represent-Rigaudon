package com.cs160.joleary.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnTouchListener, SensorEventListener {

    //private TextView mTextView;
    //private Button mFeedBtn;
    private GestureDetector mDetector;
    private DismissOverlayView mDismissOverlay;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    //code for accelerometer based off of http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

    //to be removed
    private Button shake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        shake = (Button)findViewById(R.id.shake);
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.long_press_intro);
        //mDismissOverlay.showIntroIfNecessary();


        Bundle extras = getIntent().getExtras();
        int curr=0;
        String[] people;
        String[] parties;
        int loc;
        if(extras!=null){
            loc = extras.getInt("loc");
            curr = extras.getInt("curr");
        }else{
            //change me
            loc = 12345;
            curr = 0;
        }

        //changeme
        people = getCandidates(loc)[0];
        parties = getCandidates(loc)[1];
        //changeme
        final int l = loc;
        final int c = (curr + 1) % people.length;

        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2,float velocityX, float velocityY) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("loc", l);
                i.putExtra("curr", c);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
               return true;
            }
        });

        LinearLayout pic = (LinearLayout) findViewById(R.id.piclayout);
        pic.setOnTouchListener(this);
        LinearLayout txt = (LinearLayout) findViewById(R.id.txtlayout);
        ((TextView) txt.findViewById(R.id.name)).setText(people[curr]);
        ((TextView) txt.findViewById(R.id.type)).setText(parties[curr]);
        pic.setBackgroundResource(getCandidatePic(people[curr]));
        if(parties[curr].equals("Democrat")){
            txt.setBackgroundResource(R.drawable.dembg);
        }else if(parties[curr].equals("Republican")){
            txt.setBackgroundResource(R.drawable.repbg);
        }

        Log.d("T", "curr is " + Integer.toString(curr));
        final String p = people[curr];
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "sent curr is " + p);

                Intent i = new Intent(MainActivity.this, VoteView.class);
                i.putExtra("loc", l);
                i.putExtra("prev", p);
                startActivity(i);

            }
        });
        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomizePlace();
            }
        });
/*
        mFeedBtn = (Button) findViewById(R.id.feed_btn);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String catName = extras.getString("CAT_NAME");
            mFeedBtn.setText("Feed " + catName);
        }

        mFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                startService(sendIntent);
            }
        });
        */
    }

    private int getCandidatePic(String name){
        //Change me later.
        if(name.equals("Ulysses Hiram Grant")){
            return R.drawable.pic2;
        }else if(name.equals("John Joe Doe")){
            return R.drawable.pic1;
        }else if(name.equals("Dude Bro Guy")){
            return R.drawable.pic3;
        }else if(name.equals("New guy 1")){
            return R.drawable.pic4;
        }else if(name.equals("New guy 2")){
            return R.drawable.pic5;
        }else if(name.equals("New guy 3")){
            return R.drawable.pic6;
        }
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        return mDetector.onTouchEvent(ev)  || super.onTouchEvent(ev);
    }

    private String[][] getCandidates(int zip){
        //depends on loc
        String[] people;
        String[] parties;
        if(zip==12345){
            people = new String[3];
            people[0] = "John Joe Doe";
            people[1] = "Ulysses Hiram Grant";
            people[2] = "Dude Bro Guy";
            parties = new String[3];
            parties[0] = "Democrat";
            parties[1] = "Republican";
            parties[2] = "Republican";
        }else if(zip==67890) {
            people = new String[3];
            people[0] = "New guy 1";
            people[1] = "New guy 2";
            people[2] = "New guy 3";
            parties = new String[3];
            parties[0] = "Democrat";
            parties[1] = "Democrat";
            parties[2] = "Democrat";
        }else{
            people = new String[0];
            parties = new String[0];
        }
        String[][] r = new String[2][];
        r[0] = people;
        r[1] = parties;
        return r;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    randomizePlace();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void randomizePlace(){
        int randzip = 67890;
        //send to phone
        Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        sendIntent.putExtra("shake", 1);
        sendIntent.putExtra("zip", randzip);
        startService(sendIntent);
    }
}
