package com.cs160.joleary.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailedActivity extends Activity {

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detailed);
        Bundle extras = getIntent().getExtras();
        String name;
        if(extras!=null){
            name = extras.getString("Congressman");
        }else{
            name = "None";
            //GO back
        }

        Congressman c;
        ImageView pic = (ImageView) findViewById(R.id.pic);
        //Change later
        if(name.equals("John Joe Doe")){
            c = new Congressman("John Joe Doe", "nobody@somebody.com", "www.yellowpages.com", 12345, "@nobody", "Something Something something tweet", R.drawable.pic1, "Democrat", "Representative");
            pic.setImageResource(R.drawable.pic1large);
        }else if(name.equals("Ulysses Hiram Grant")){
            c = new Congressman("Ulysses Hiram Grant", "dedguy@yahoo.com", "www.usgrant.com", 12345, "@someoneelse", "It sure sucks being dead!", R.drawable.pic2, "Republican", "House");
            pic.setImageResource(R.drawable.pic2large);
        }else if(name.equals("New guy 1")) {
            c = new Congressman("New guy 1", "nobody@somebody.com", "www.yellowpages.com", 67890, "@nobody", "Something Something something tweet", R.drawable.pic4, "Democrat", "Representative");
            pic.setImageResource(R.drawable.pic4large);
        }else if(name.equals("New guy 2")) {
            c = new Congressman("New guy 2", "dedguy@yahoo.com", "www.usgrant.com", 67890, "@someoneelse", "It sure sucks being dead!", R.drawable.pic5, "Democrat", "House");
            pic.setImageResource(R.drawable.pic5large);
        }else if(name.equals("New guy 3")) {
            c = new Congressman("Ulysses Hiram Grant", "dedguy@yahoo.com", "www.usgrant.com", 12345, "@someoneelse", "It sure sucks being dead!", R.drawable.pic6, "Republican", "House");
            pic.setImageResource(R.drawable.pic6large);
        }else if(name.equals("Bro Dude Guy")){
            c = new Congressman("New guy 3", "bro@yoloswag.com", "www.dudchan.com", 67890, "@guybro", "Dude I'm a politician bro", R.drawable.pic6, "Democrat", "House");
            pic.setImageResource(R.drawable.pic3large);
        }else{
            c = new Congressman("");
        }

        RelativeLayout piclayout = (RelativeLayout) findViewById(R.id.picLayout);
        if(c.getParty().equals("Republican")){
            piclayout.setBackgroundResource(R.drawable.repbg);
        }else if(c.getParty().equals("Democrat")){
            piclayout.setBackgroundResource(R.drawable.demobg);
        }
        final int z = c.getZip();
        backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedActivity.this, CongressionalActivity.class);
                i.putExtra("Zip", Integer.toString(z));
                startActivity(i);
            }
        });
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.picLayout);
        LinearLayout details = getLayoutFromCongressman(c);
        parent.addView(details, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    private LinearLayout getLayoutFromCongressman(Congressman c){
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout details = (LinearLayout) inflater.inflate(R.layout.detailed_subview, null, false);

        ((TextView)details.findViewById(R.id.name)).setText(c.getName());
        ((TextView)details.findViewById(R.id.type)).setText(c.getParty()+" "+c.getType());
        ((TextView)details.findViewById(R.id.eot)).setText(c.getEOT());
        ((TextView)details.findViewById(R.id.billlist)).setText(c.getBills());
        ((TextView)details.findViewById(R.id.comlist)).setText(c.getCommittees());
        return details;
    }
}
