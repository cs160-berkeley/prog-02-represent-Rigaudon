package com.cs160.joleary.represent;

/**
 * Created by Michael on 3/3/2016.
 */
public class Location {

    private String county;
    private int zip;
    private String state;
    private int obamaVote;
    private int romneyVote;

    public Location(int zip){
        //changeme
        if(zip==12345){
            this.zip = zip;
            this.county = "Fake County";
            this.state = "FL";
            this.obamaVote = 64;
            this.romneyVote = 36;
        }else if(zip==67890){
            this.zip = zip;
            this.county = "Real County";
            this.state = "AZ";
            this.obamaVote = 21;
            this.romneyVote = 79;
        }
    }

    public String getCounty(){return this.county;}
    public int getZip(){return this.zip;}
    public String getState(){return this.state;}
    public int getObamaVote(){return this.obamaVote;}
    public int getRomneyVote(){return this.romneyVote;}
}

