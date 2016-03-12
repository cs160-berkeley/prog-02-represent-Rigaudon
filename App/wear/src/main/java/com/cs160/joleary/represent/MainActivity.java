package com.cs160.joleary.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private Double obama, romney;
    public static Activity fa;
    //code for accelerometer based off of http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125

    //to be removed
    private Button shake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.fa!=null){
            MainActivity.fa.finish();
        }
        fa = this;
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
        String[] images;
        String data;
        if(extras!=null){
            data = extras.getString("loc");
            curr = extras.getInt("curr");
        }else{
            //change me
            data = "";
            curr = 0;
        }

        Log.d("T","data is "+data);
        //changeme
        String[][] candidates = getCandidates(data);
        String county,state,zip;
        people = candidates[0];
        parties = candidates[1];
        images = candidates[2];
        obama = Double.parseDouble(candidates[3][0]);
        romney = Double.parseDouble(candidates[3][1]);
        Log.d("T", "obama is "+obama+", romney is "+romney);
        zip = candidates[4][0];
        state = candidates[4][1];
        county = candidates[4][2];
        Log.d("T",county+state+zip);
        //changeme
        final String l = data;
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

        new ImageLoadTask(images[curr],pic).execute();

        if(parties[curr].equals("Democrat")){
            txt.setBackgroundResource(R.drawable.dembg);
        }else if(parties[curr].equals("Republican")){
            txt.setBackgroundResource(R.drawable.repbg);
        }else{
            //TODO: indep
            txt.setBackgroundResource(R.drawable.repbg);
        }

        Log.d("T", "curr is " + Integer.toString(curr));
        final String p = people[curr];
        final String s = state;
        final String cnty = county;
        final String z = zip;
        final Double o = obama;
        final Double r = romney;
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "sent curr is " + p);

                Intent i = new Intent(MainActivity.this, VoteView.class);
                i.putExtra("loc", l);
                i.putExtra("prev", p);
                i.putExtra("state", s);
                i.putExtra("county", cnty);
                i.putExtra("zip", z);
                i.putExtra("obama", o);
                i.putExtra("romney",r);
                startActivity(i);

            }
        });
        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomizePlace();
            }
        });

    }


    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private View v;

        public ImageLoadTask(String url, View v) {
            this.url = url;
            this.v = v;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //result = Bitmap.createScaledBitmap(result,130,160,false);
            v.setBackground(new BitmapDrawable(result));
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        return mDetector.onTouchEvent(ev)  || super.onTouchEvent(ev);
    }

    private String[][] getCandidates(String data){
        String[] splitdata = data.split(";");
        int count = Integer.parseInt(splitdata[0]);
        String[] people = new String[count];
        String[] parties = new String[count];
        String[] images = new String[count];

        for(int i=0;i<count;i++){
            people[i] = splitdata[i*3+1];
            parties[i] = splitdata[i*3+2];
            images[i] = splitdata[i*3+3];
        }
        String[][] r = new String[5][];
        r[0] = people;
        r[1] = parties;
        r[2] = images;
        r[3] = new String[2];
        r[4] = new String[3];
        r[3][0] = splitdata[splitdata.length-2];
        r[3][1] = splitdata[splitdata.length-1];
        r[4][0] = splitdata[splitdata.length-3];
        r[4][1] = splitdata[splitdata.length-4];
        r[4][2] = splitdata[splitdata.length-5];
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
        int randzip = 52342;
        //send to phone
        //int randzip = Math.round(Math.random()*90000+10000);

        Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        sendIntent.putExtra("shake", 1);
        sendIntent.putExtra("zip", randzip);
        startService(sendIntent);
/*
        Intent sendIntent2 = getIntent();
        sendIntent2.putExtra("loc", randzip);
        sendIntent2.putExtra("curr", 0);
        startActivity(sendIntent2);
*/
    }
}
