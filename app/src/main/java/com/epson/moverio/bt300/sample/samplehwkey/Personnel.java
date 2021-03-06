package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 3/27/2018.
 */


public class Personnel implements Serializable{

    private String c_fname;
    private String c_lname;
    private String c_code;
    private String c_email;
    private String c_image;
    private String c_id;

    public Personnel(String code,String fname, String lname, String email,String image, String id){
        this.c_fname = fname;
        this.c_lname = lname;
        this.c_code = code;
        this.c_email = email;
        this.c_image = image;
        this.c_id = id;
    }

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

    public String getC_email() { return c_email; }

    public void setC_email(String c_email) { this.c_email = c_email; }

    public String getC_image() { return c_image; }

    public void setC_image(String c_image) { this.c_image = c_image; }

    public String getC_id() {
        return c_id;
    }
}
