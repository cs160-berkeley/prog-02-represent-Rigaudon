package com.cs160.joleary.represent;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "8h6NV1UplqlPrN4xaV8KEze8B";
    private static final String TWITTER_SECRET = "u6sQmfkWBLm3k7UVRcDgJpuYNLHYAur22xZbPHKsPSaYMIFY77";
    private static final String GOOGLEAPIKEY = "AIzaSyBzn_Sgiit8D9bLKokW6A9ti3WwAyWH6xM";
    //there's not much interesting happening. when the buttons are pressed, they start
    //the PhoneToWatchService with the cat name passed in.

    private Button searchButton;
    private Button currLocButton;
    private EditText zip;
    private ImageView err;
    private ImageView detected;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Double latitude;
    private Double longitude;
    private TwitterLoginButton loginButton;
    private String county, state;
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.fa!=null){
            MainActivity.fa.finish();
        }
        fa = this;
        Bundle extras = getIntent().getExtras();
        String autogo = null;
        if(extras!=null){
            autogo = extras.getString("Zip");
        }
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


        searchButton = (Button) findViewById(R.id.search);
        currLocButton = (Button) findViewById(R.id.currloc);
        zip = (EditText) findViewById(R.id.zip);
        err = (ImageView) findViewById(R.id.err);
        detected = (ImageView) findViewById(R.id.detected);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        zip.setTypeface(font);

        if(autogo!=null){
            zip.setText(autogo);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String z = zip.getText().toString();
                String getzipurl = "https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:"+z+"&key="+GOOGLEAPIKEY;
                new getLatLong().execute(getzipurl);

            }
        });

        currLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zip.setText(detectZip());
            }
        });


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean checkValidZip(String s){
        if(s.length()==5){
            return true;
        }else {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(err, "alpha", 0f, 1f);
            fadeIn.setDuration(1000);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(err, "alpha", 1f, 0f);
            fadeOut.setDuration(1000);
            final AnimatorSet a = new AnimatorSet();
            a.play(fadeOut).after(fadeIn);
            a.start();
            return false;
        }
    }

    private String detectZip(){
        //Add "error detecting zip"
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(detected, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(detected, "alpha", 1f, 0f);
        fadeOut.setDuration(1000);
        final AnimatorSet a = new AnimatorSet();
        a.play(fadeOut).after(fadeIn);
        Log.i("T", longitude + " " + latitude);
        if(mGoogleApiClient==null){
            Log.i("T", "client null");
            return "12345";
        }else{
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
                longitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
            }
            try {
                List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
                a.start();
                String found = address.get(0).getPostalCode();

                if(found!=null){
                    return found;
                }else{
                    return "67890";
                }

            }catch(Exception e){
                return "67890";
            }
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("T", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("T", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    class getLatLong extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                InputStream is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        private String readIt(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        protected void onPostExecute(String result) {
            try{
                JSONObject reader = new JSONObject(result);
                JSONObject c = (JSONObject)reader.getJSONArray("results").get(0);
                JSONObject d = c.getJSONObject("geometry").getJSONObject("location");
                latitude = Double.parseDouble(d.getString("lat"));
                longitude = Double.parseDouble(d.getString("lng"));
                Log.d("T", "latlng is "+latitude+','+longitude);
                String z = zip.getText().toString();
                if (checkValidZip(z)) {
                    Intent i = new Intent(MainActivity.this, CongressionalActivity.class);
                    i.putExtra("Zip", z);
                    final String latlng = latitude+","+longitude;
                    i.putExtra("latlng", latlng);
                    startActivity(i);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
