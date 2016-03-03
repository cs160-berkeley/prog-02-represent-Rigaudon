package com.cs160.joleary.represent;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
    //there's not much interesting happening. when the buttons are pressed, they start
    //the PhoneToWatchService with the cat name passed in.

    //private Button mFredButton;
    //private Button mLexyButton;

    private Button searchButton;
    private Button currLocButton;
    private EditText zip;
    private ImageView err;
    private ImageView detected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        searchButton = (Button) findViewById(R.id.search);
        currLocButton = (Button) findViewById(R.id.currloc);
        zip = (EditText) findViewById(R.id.zip);
        err = (ImageView) findViewById(R.id.err);
        detected = (ImageView) findViewById(R.id.detected);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        zip.setTypeface(font);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String z = zip.getText().toString();
                if (checkValidZip(z)) {

                    Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                    sendIntent.putExtra("loc", z);
                    startService(sendIntent);

                    Intent i = new Intent(MainActivity.this, CongressionalActivity.class);
                    i.putExtra("Zip", z);
                    startActivity(i);
                }
            }
        });

        currLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zip.setText(detectZip());
            }
        });
/*
        mFredButton = (Button) findViewById(R.id.fred_btn);
        mLexyButton = (Button) findViewById(R.id.lexy_btn);

        mFredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("CAT_NAME", "Fred");
                startService(sendIntent);
            }
        });

        mLexyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("CAT_NAME", "Lexy");
                startService(sendIntent);
            }
        });
*/
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
        a.start();
        return "12345";
    }
/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
