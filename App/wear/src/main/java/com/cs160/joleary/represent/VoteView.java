package com.cs160.joleary.represent;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cs160.joleary.represent.R;

public class VoteView extends Activity {

    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VoteView.fa!=null){
            VoteView.fa.finish();
        }
        fa = this;
        setContentView(R.layout.activity_vote_view);
        Bundle extras = getIntent().getExtras();
        String loc = "";
        String p = "";
        String state="", zip="", county="";
        Double romney=0.0;
        Double obama=0.0;
        if(extras!=null){
            loc = extras.getString("loc");
            p = extras.getString("prev");
            state = extras.getString("state");
            county = extras.getString("county");
            zip = extras.getString("zip");
            romney = extras.getDouble("romney");
            obama = extras.getDouble("obama");
        }
        final String data = loc;
        RelativeLayout votelayout = (RelativeLayout)findViewById(R.id.vote);

                votelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VoteView.this, MainActivity.class);
                if(MainActivity.fa!=null) {
                    MainActivity.fa.finish();
                }
                i.putExtra("loc", data);
                i.putExtra("curr", 0);
                startActivity(i);
            }
        });

        Log.d("T", "Intent curr is " + p);
        Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
        sendIntent.putExtra("shake",0);
        sendIntent.putExtra("Congressman", p);
        startService(sendIntent);

        //change: switch places of "2012 vote results" and state/county views
        ((TextView)votelayout.findViewById(R.id.countystate)).setText(county + "\n" + state+" "+zip);
        ((TextView)votelayout.findViewById(R.id.obamapercent)).setText(Double.toString(obama)+"%");
        ((TextView)votelayout.findViewById(R.id.romneypercent)).setText(Double.toString(romney)+"%");
        ((ImageView)votelayout.findViewById(R.id.obamabar)).getLayoutParams().width = obama.intValue()*2;
        ((ImageView)votelayout.findViewById(R.id.obamabar)).getLayoutParams().height = 20;
        ((ImageView)votelayout.findViewById(R.id.romneybar)).getLayoutParams().width = romney.intValue()*2;
        ((ImageView)votelayout.findViewById(R.id.romneybar)).getLayoutParams().height = 20;
    }

}
