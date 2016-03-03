package com.cs160.joleary.represent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cs160.joleary.represent.R;

import org.w3c.dom.Text;

public class CongressionalActivity extends Activity {


    private TextView toptext;
    private TextView zipdisplay;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_congressional);
        toptext = (TextView) findViewById(R.id.toptext);
        zipdisplay = (TextView) findViewById(R.id.zipdisplay);

        Bundle extras = getIntent().getExtras();
        String zip = "";
        if(extras!=null){
            zip = extras.getString("Zip");
            zipdisplay.setText(zip);
        }


        toptext.setTypeface(font);
        zipdisplay.setTypeface(font);

        backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CongressionalActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        LinearLayout parent = (LinearLayout) findViewById(R.id.parentLayout);
        Congressman[] cong = new Congressman[3];
        if(zip.equals("12345")){
            cong[0] = new Congressman("John Joe Doe", "nobody@somebody.com", "www.yellowpages.com", 12345, "@nobody", "Something Something something tweet", R.drawable.pic1, "Democrat", "Representative");
            cong[1] = new Congressman("Ulysses Hiram Grant", "dedguy@yahoo.com", "www.usgrant.com", 12345, "@someoneelse", "It sure sucks being dead!", R.drawable.pic2, "Republican", "House");
            cong[2] = new Congressman("Bro Dude Guy", "bro@yoloswag.com", "www.dudchan.com", 12345, "@guybro", "Dude I'm a politician bro", R.drawable.pic3, "Republican", "Representative");
        }else if(zip.equals("67890")){
            cong[0] = new Congressman("New guy 1", "nobody@somebody.com", "www.yellowpages.com", 67890, "@nobody", "Something Something something tweet", R.drawable.pic4, "Democrat", "Representative");
            cong[1] = new Congressman("New guy 2", "dedguy@yahoo.com", "www.usgrant.com", 67890, "@someoneelse", "It sure sucks being dead!", R.drawable.pic5, "Democrat", "House");
            cong[2] = new Congressman("New guy 3", "bro@yoloswag.com", "www.dudchan.com", 67890, "@guybro", "Dude I'm a politician bro", R.drawable.pic6, "Democrat", "House");
        }

        RelativeLayout l1 = createViewFromCongressman(cong[0]);
        parent.addView(l1);
        RelativeLayout l2 = createViewFromCongressman(cong[1]);
        parent.addView(l2);
        RelativeLayout l3 = createViewFromCongressman(cong[2]);
        parent.addView(l3);

    }

    private RelativeLayout createViewFromCongressman(Congressman c){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout lo = (RelativeLayout) inflater.inflate(R.layout.congressional_subview, null, false);

        TextView name = (TextView) lo.findViewById(R.id.name);
        name.setText(c.getName() + ": "+c.getType());
        name.setTypeface(font);

        TextView party = (TextView) lo.findViewById(R.id.party);
        party.setText(c.getParty());
        party.setTypeface(font);

        TextView twitter1 = (TextView) lo.findViewById(R.id.twitter1);
        twitter1.setText(c.getTwitter());
        //twitter1.setTypeface(font);

        TextView twitter2 = (TextView) lo.findViewById(R.id.twitter2);
        twitter2.setText(c.getTwitterMsg());
        //twitter2.setTypeface(font);

        TextView email = (TextView) lo.findViewById(R.id.email);
        email.setText(c.getEmail());
        //email.setTypeface(font);

        TextView website = (TextView) lo.findViewById(R.id.website);
        website.setText(c.getWebsite());
        //website.setTypeface(font);

        ImageView pic = (ImageView) lo.findViewById(R.id.picture);
        //Change later
        pic.setImageResource(c.getImage());

        RelativeLayout container = (RelativeLayout) lo.findViewById(R.id.repcontainer);
        if(c.getParty().equals("Republican")){
            container.setBackgroundResource(R.drawable.republicanbg);
        }else{
            container.setBackgroundResource(R.drawable.democratbg);
        }

        Button infobutton = (Button) lo.findViewById(R.id.info);
        final String curr = c.getName();
        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CongressionalActivity.this, DetailedActivity.class);
                i.putExtra("Congressman", curr);
                startActivity(i);
            }
        });

        return lo;
    }

}
