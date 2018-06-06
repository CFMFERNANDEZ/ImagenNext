package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

public class Defects implements Serializable {
    private String c_id;
    private String c_dscr;
    private String c_code;
    private String c_parent;

    public Defects (String c_id, String c_dscr, String c_code, String c_parent){
        this.c_id = c_id;
        this.c_code = c_code;
        this.c_dscr = c_dscr;
        this.c_parent = c_parent;
    }

    public String getC_id() { return c_id; }

    public void setC_id(String c_id) { this.c_id = c_id; }

    public String getC_dscr() { return c_dscr; }

    public void setC_dscr(String c_dscr) { this.c_dscr = c_dscr; }

    public String getC_code() { return c_code; }

    public void setC_code(String c_code) { this.c_code = c_code; }

    public String getC_parent() {
        return c_parent;
    }
}
