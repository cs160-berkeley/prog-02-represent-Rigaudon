package com.cs160.joleary.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class CongressionalActivity extends Activity {


    private TextView toptext;
    private TextView zipdisplay;
    private Button backButton;
    private final String SUNLIGHT_APIKEY = "c31cda10dab1449e9d866374ba7a153e";
    private String zip, county="", state="";
    private static final String TWITTER_KEY = "8h6NV1UplqlPrN4xaV8KEze8B";
    private static final String TWITTER_SECRET = "u6sQmfkWBLm3k7UVRcDgJpuYNLHYAur22xZbPHKsPSaYMIFY77";
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CongressionalActivity.fa!=null){
            CongressionalActivity.fa.finish();
        }
        fa = this;
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_congressional);
        toptext = (TextView) findViewById(R.id.toptext);
        zipdisplay = (TextView) findViewById(R.id.zipdisplay);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        String latlng="";
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            zip = extras.getString("Zip");
            latlng = extras.getString("latlng");
            zipdisplay.setText(zip);
        }

        String countyurl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng+"&sensor=false";
        Log.d("T", countyurl);
        new getCounty().execute(countyurl);

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

        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=";
        url += zip;
        url += "&apikey="+SUNLIGHT_APIKEY;

        new doApiCall().execute(url);
        //Log.d("T", "The result is: " + result);



    }

    private RelativeLayout createViewFromCongressman(Congressman c){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Byington.ttf");
        LayoutInflater inflater = LayoutInflater.from(this);
        final RelativeLayout lo = (RelativeLayout) inflater.inflate(R.layout.congressional_subview, null, false);
        final Congressman curr = new Congressman(c);
        TextView name = (TextView) lo.findViewById(R.id.name);
        name.setText(c.getName() + ": "+c.getType());
        name.setTypeface(font);

        TextView party = (TextView) lo.findViewById(R.id.party);
        party.setText(c.getParty());
        party.setTypeface(font);


        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.userTimeline(null, c.getTwitter(), 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result, which provides a Tweet inside of result.data
                List<Tweet> tweets = result.data;
                Tweet latest = tweets.get(0);
                long id = latest.getId();
                final TextView twitter2 = (TextView) lo.findViewById(R.id.twitter2);

                //twitter2.setText(Html.fromHtml(latest.text).toString());
                ImageView pic = (ImageView) lo.findViewById(R.id.picture);
                String imgurl = "https://theunitedstates.io/images/congress/225x275/"+curr.id+".jpg";
                curr.setImgUrl(imgurl);
                new ImageLoadTask(imgurl, pic).execute();
                TweetUtils.loadTweet(id, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        CompactTweetView tweetView = new CompactTweetView(CongressionalActivity.this, result.data);
                        tweetView.setLayoutParams(twitter2.getLayoutParams());
                        lo.addView(tweetView);

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("TwitterKit", "Load Tweet failure", exception);
                    }
                });

            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Log.d("T", exception.toString());
            }
        });

        TextView email = (TextView) lo.findViewById(R.id.email);
        email.setText(c.getEmail());
        //email.setTypeface(font);

        TextView website = (TextView) lo.findViewById(R.id.website);
        website.setText(c.getWebsite());
        //website.setTypeface(font);

        RelativeLayout container = (RelativeLayout) lo.findViewById(R.id.repcontainer);
        if(c.getParty().equals("Republican")){
            container.setBackgroundResource(R.drawable.republicanbg);
        }else if(c.getParty().equals("Democrat")){
            container.setBackgroundResource(R.drawable.democratbg);
        }else{
            container.setBackgroundResource(R.drawable.independentbg);
        }

        Button infobutton = (Button) lo.findViewById(R.id.info);

        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CongressionalActivity.this, DetailedActivity.class);
                i.putExtra("Congressman", curr.getName());
                startActivity(i);

            }
        });

        return lo;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
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
            result = Bitmap.createScaledBitmap(result,130,160,false);
            imageView.setImageBitmap(result);
        }

    }

    class doApiCall extends AsyncTask<String, Void, String> {

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
                Log.d("T", "The response is: " + response);
                InputStream is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                Log.d("T", "the result is :"+contentAsString);
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

            LinearLayout parent = (LinearLayout) findViewById(R.id.parentLayout);
            try{
                JSONObject reader = new JSONObject(result);
                int count = reader.getInt("count");
                Congressman[] cong = new Congressman[count];
                String sendstring = "";
                sendstring+= Integer.toString(count)+";";


                for(int i=0;i<count;i++){
                    JSONObject c = (JSONObject)reader.getJSONArray("results").get(i);
                    String name = c.getString("first_name")+" "+c.getString("last_name");
                    String email = c.getString("oc_email");
                    String website = c.getString("website");
                    //zip is just zip
                    String twitterid = c.getString("twitter_id");
                    String party = c.getString("party");
                    if(party.equals("D")){
                        party = "Democrat";
                    }else if(party.equals("R")){
                        party = "Republican";
                    }else{
                        party = "Independent";
                    }
                    String type = c.getString("title");
                    if(type.equals("Rep")){
                        type = "Representative";
                    }else if(type.equals("Sen")){
                        type = "Senator";
                    }
                    String end = c.getString("term_end");
                    String id = c.getString("bioguide_id");
                    String imgurl = "https://theunitedstates.io/images/congress/225x275/"+id+".jpg";
                    cong[i] = new Congressman(id, name, email, website, Integer.parseInt(zip), twitterid, party, type, end);

                    //save to file
                    String filename = name;
                    File secondFile = new File(getFilesDir()+"/",filename+".data");
                    String outputString = "";
                    outputString = id+";"+name+";"+email+";"+website+";"+zip+";"+twitterid+";"+party+";"+type+";"+end;
                    secondFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(secondFile);

                    fos.write(outputString.getBytes());
                    fos.flush();
                    fos.close();
                    Log.d("T", "MADE FILE");


                    sendstring+=name+";"+party+";"+imgurl+";";
                    RelativeLayout l = createViewFromCongressman(cong[i]);
                    parent.addView(l);
                }

                InputStream votedata = getAssets().open("json/newelectioncounty2012.json");
                JSONObject votes = new JSONObject(readIt(votedata));
                JSONObject d = votes.getJSONObject(county + ", " + state);
                sendstring+=county+";"+state+";"+zip+";";
                sendstring+=d.getString("obama")+";"+d.getString("romney");
                Log.d("T", "SEND STRING IS "+sendstring);
                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("loc", sendstring);
                startService(sendIntent);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class getCounty extends AsyncTask<String, Void, String> {

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
                JSONArray d = c.getJSONArray("address_components");
                int i = 0;
                try {
                    while (county.equals("") || state.equals("")) {
                        JSONObject e = (JSONObject) d.get(i);
                        if (e.getJSONArray("types").get(0).equals("administrative_area_level_2")) {
                            county = e.getString("long_name");
                        }
                        if (e.getJSONArray("types").get(0).equals("administrative_area_level_1")) {
                            state = e.getString("short_name");
                        }
                        i++;
                    }
                    Log.d("T", "county is " + county + ", " + state);
                }catch(IndexOutOfBoundsException e){
                    county = "Error";
                    state = "Error";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
