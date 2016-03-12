package com.cs160.joleary.represent;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Michael on 3/1/2016.
 */
public class Congressman implements Serializable{

    public String id;
    public String imgurl;
    private String name;
    private String email;
    private String website;
    private int zip;
    private String twitter;
    private String party;
    private String type;
    private String endDate;

    public Congressman(String name){
        this.name = name;
        //other stuff goes here

    }
    public Congressman(Congressman c){
        this.id = c.id;
        this.name = c.name;
        this.email = c.email;
        this.website = c.website;
        this.zip = c.zip;
        this.twitter = c.twitter;
        this.party = c.party;
        this.type = c.type;
        this.endDate = c.endDate;
    }
    public Congressman(String id, String name, String email, String website, int zip, String twitter, String party, String type, String end){
        this.id = id;
        this.name = name;
        this.email = email;
        this.website = website;
        this.zip = zip;
        this.twitter = twitter;
        this.party = party;
        this.type = type;
        this.endDate = end;

    }
    public void setImgUrl(String url){
        this.imgurl = url;
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

}
