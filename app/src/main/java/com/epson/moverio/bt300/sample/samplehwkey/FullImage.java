package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 6/4/2018.
 */

public class FullImage {

    public String recId;
    public String database;
    public String lang;
    public String image;

    public FullImage(String recId, String database, String lang, String image){
        this.recId = recId;
        this.database = database;
        this.lang = lang;
        this.image = image;
    }
}