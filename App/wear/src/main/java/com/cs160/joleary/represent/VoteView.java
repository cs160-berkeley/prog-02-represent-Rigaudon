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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_view);
        Bundle extras = getIntent().getExtras();
        int loc = 0;
        String p = "";
        if(extras!=null){
            loc = extras.getInt("loc");
            p = extras.getString("prev");
        }
        Location l = new Location(loc);
        RelativeLayout votelayout = (RelativeLayout)findViewById(R.id.vote);
        final int zip = loc;

        votelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VoteView.this, MainActivity.class);
                i.putExtra("loc", zip);
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
        ((TextView)votelayout.findViewById(R.id.countystate)).setText(l.getCounty() + "\n" + l.getState()+" "+l.getZip());
        ((TextView)votelayout.findViewById(R.id.obamapercent)).setText(Integer.toString(l.getObamaVote())+"%");
        ((TextView)votelayout.findViewById(R.id.romneypercent)).setText(Integer.toString(l.getRomneyVote())+"%");
        ((ImageView)votelayout.findViewById(R.id.obamabar)).getLayoutParams().width = l.getObamaVote()*2;
        ((ImageView)votelayout.findViewById(R.id.obamabar)).getLayoutParams().height = 20;
        ((ImageView)votelayout.findViewById(R.id.romneybar)).getLayoutParams().width = l.getRomneyVote()*2;
        ((ImageView)votelayout.findViewById(R.id.romneybar)).getLayoutParams().height = 20;
    }

}
