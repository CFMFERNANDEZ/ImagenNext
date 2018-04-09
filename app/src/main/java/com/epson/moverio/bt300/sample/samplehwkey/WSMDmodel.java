package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 3/27/2018.
 */

public class WSMDmodel implements Serializable{

    private String c_code;
    private String c_ws;
    private String c_id;
    private String c_uniquecode;
    private String c_dscr;

    public WSMDmodel(String c_code,String c_ws,String c_id,String c_uniquecode,String c_dscr){
        this.c_code = c_code;
        this.c_ws = c_ws;
        this.c_id = c_id;
        this.c_uniquecode = c_uniquecode;
        this.c_dscr = c_dscr;
    }

    public String getC_code() {
        return c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public String getC_ws() {
        return c_ws;
    }

    public void setC_ws(String c_ws) {
        this.c_ws = c_ws;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_uniquecode() {
        return c_uniquecode;
    }

    public void setC_uniquecode(String c_uniquecode) {
        this.c_uniquecode = c_uniquecode;
    }

    public String getC_dscr() {
        return c_dscr;
    }

    public void setC_dscr(String c_dscr) {
        this.c_dscr = c_dscr;
    }
}
