package com.cs160.joleary.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailedActivity extends Activity {

    private Button backButton;
    private final String SUNLIGHT_APIKEY = "c31cda10dab1449e9d866374ba7a153e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detailed);
        Bundle extras = getIntent().getExtras();

        String congname = extras.getString("Congressman");
        //read
        Congressman c;
        try {
            //FileInputStream f_in = new FileInputStream(getFilesDir()+"/"+"test"+".data");

            File secondInputFile = new File(getFilesDir() + "/", congname+".data");
            InputStream secondInputStream = new FileInputStream(secondInputFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            r.close();
            secondInputStream.close();
            Log.d("T", "File contents: " + total);
            String[] congdata = total.toString().split(";");
            c = new Congressman(congdata[0],congdata[1],congdata[2],congdata[3],Integer.parseInt(congdata[4]),congdata[5],congdata[6],congdata[7],congdata[8]);
        }catch(Exception e){
            Log.d("T", "error");
            c = new Congressman("");
            e.printStackTrace();
        }
        //Congressman c = (Congressman) getIntent().getSerializableExtra("Congressman");

        RelativeLayout piclayout = (RelativeLayout) findViewById(R.id.picLayout);
        String imgurl = "https://theunitedstates.io/images/congress/225x275/"+c.id+".jpg";
        new ImageLoadTask(imgurl,(ImageView)findViewById(R.id.pic)).execute();
        if(c.getParty().equals("Republican")){
            piclayout.setBackgroundResource(R.drawable.repbg);
        }else if(c.getParty().equals("Democrat")){
            piclayout.setBackgroundResource(R.drawable.demobg);
        }else{
            piclayout.setBackgroundResource(R.drawable.indepbg);
        }
        final int z = c.getZip();
        backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedActivity.this, CongressionalActivity.class);
                if(CongressionalActivity.fa!=null) {
                    CongressionalActivity.fa.finish();
                }
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
        ((TextView)details.findViewById(R.id.type)).setText(c.getParty() + " " + c.getType());
        ((TextView)details.findViewById(R.id.eot)).setText(c.getEOT());
        String comurl = "http://congress.api.sunlightfoundation.com/committees?member_ids="+c.id+"&apikey="+SUNLIGHT_APIKEY;
        new getComs((TextView)details.findViewById(R.id.comlist)).execute(comurl);
        String billurl = "http://congress.api.sunlightfoundation.com/bills?sponsor_id="+c.id+"&apikey="+SUNLIGHT_APIKEY;
        new getBills((TextView)details.findViewById(R.id.billlist)).execute(billurl);
        //((TextView)details.findViewById(R.id.comlist)).setText(coms);
        return details;
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
            result = Bitmap.createScaledBitmap(result,370,450,false);
            imageView.setImageBitmap(result);
        }
    }

    class getComs extends AsyncTask<String, Void, String> {
        private Exception exception;
        private TextView view;
        public getComs(TextView v){
            this.view = v;
        }
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
            try{
                JSONObject reader = new JSONObject(result);
                //changeme
                int count = Math.min(reader.getInt("count"), 8);
                //count = reader.getInt("count");
                String coms = "";
                for(int i=0;i<count;i++){
                    JSONObject c = (JSONObject)reader.getJSONArray("results").get(i);
                    coms+=c.getString("name")+"\n\n";
                }
                view.setText(coms);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class getBills extends AsyncTask<String, Void, String> {
        private Exception exception;
        private TextView view;
        public getBills(TextView v){
            this.view = v;
        }
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
            try{
                JSONObject reader = new JSONObject(result);
                //changeme
                int count = Math.min(reader.getInt("count"), 8);
                //count = reader.getInt("count");
                String bills = "";
                for(int i=0;i<count;i++){
                    JSONObject c = (JSONObject)reader.getJSONArray("results").get(i);
                    bills+="Bill "+c.getString("number")+"\n"+c.getString("official_title")+"\n\n";
                }
                view.setText(bills);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
