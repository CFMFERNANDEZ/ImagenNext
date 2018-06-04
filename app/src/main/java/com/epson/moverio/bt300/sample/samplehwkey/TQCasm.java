package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 5/31/2018.
 */

public class TQCasm {

    private String database;
    private String lang;
    private String image;
    private String personnel;

    private String fwork;
    private String mfgOrder;
    private String availIssue;
    private String priority;
    private String defect;

    public TQCasm(String database, String lang, String image, String fwork, String mfgOrder, String personnel, String availIssue, String priority, String defect){
        this.database = database;
        this. lang = lang;
        this.image = image;
        this.fwork = fwork;
        this.mfgOrder = mfgOrder;
        this.personnel = personnel;
        this.availIssue = availIssue;
        this.priority = priority;
        this.defect = defect;
    }
}
