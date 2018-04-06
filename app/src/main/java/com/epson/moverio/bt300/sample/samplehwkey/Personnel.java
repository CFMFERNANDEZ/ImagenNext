package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/27/2018.
 */


public class Personnel {

    private String c_fname;
    private String c_lname;
    private String c_code;
    private String c_id;

    public Personnel(String code,String fname, String lname, String id){
        this.c_fname = fname;
        this.c_lname = lname;
        this.c_code = code;
        this.c_id = id;
    }

    public String getId() { return c_id; }

    public String getC_fname() {
        return c_fname;
    }

    public void setC_fname(String c_fname) {
        this.c_fname = c_fname;
    }

    public String getC_lname() {
        return c_lname;
    }

    public void setC_lname(String c_lname) {
        this.c_lname = c_lname;
    }

    public String getC_code() {
        return c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }
}
