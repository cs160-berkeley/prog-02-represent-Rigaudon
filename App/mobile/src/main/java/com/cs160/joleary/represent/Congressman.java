package com.cs160.joleary.represent;

import android.graphics.drawable.Drawable;

/**
 * Created by Michael on 3/1/2016.
 */
public class Congressman {

    private String name;
    private String email;
    private String website;
    private int zip;
    private String twitter;
    private String twittermsg;
    private int image;
    private String party;
    private String type;
    private String endDate;
    private String[] bills;
    private String[] committees;

    public Congressman(String name){
        this.name = name;
        //other stuff goes here

    }

    public Congressman(String name, String email, String website, int zip, String twitter, String twittermsg, int image, String party, String type){
        this.name = name;
        this.email = email;
        this.website = website;
        this.zip = zip;
        this.twitter = twitter;
        this.twittermsg = twittermsg;
        this.image = image;
        this.party = party;
        this.type = type;
        this.endDate = "January 1, 1990";
        this.committees = new String[2];
        this.committees[0]= "Committee on Armed Services: \n -Subcommittee on Emerging Threats and Capabilities \n -Subcommittee on Seapower \n -Subcommittee on Strategic Forces";
        this.committees[1]= "Committee on Commerce & Science: \n -Subcommittee on Space, Science, and Competitiveness, Chairman \n -Subcommittee on Aviation Operations";
        this.bills = new String[3];
        this.bills[0] = "S.1234 - 114th Congress (2015-2016) \n A bill to do nothing for anybody or to do everything for nobody. In any case, it doesn't really do anything.";
        this.bills[1] = "S.1234 - 114th Congress (2015-2016) \n A bill to do nothing for anybody or to do everything for nobody. In any case, it doesn't really do anything.";
        this.bills[2] = "S.1234 - 114th Congress (2015-2016) \n A bill to do nothing for anybody or to do everything for nobody. In any case, it doesn't really do anything.";

    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public String getEOT(){ return this.endDate;}
    public String getParty(){
        return this.party;
    }
    public String getBills() {
        //change me later
        return this.bills[0]+"\n"+this.bills[1]+"\n"+this.bills[2];
    }
    public String getCommittees(){
        //change me later
        return this.committees[0]+"\n"+this.committees[1];
    }
    public String getType(){
        return this.type;
    }

    public String getWebsite(){
        return this.website;
    }

    public int getZip(){
        return this.zip;
    }

    public String getTwitter(){
        return this.twitter;
    }

    public String getTwitterMsg(){
        return this.twittermsg;
    }

    public int getImage(){
        return this.image;
    }
}
