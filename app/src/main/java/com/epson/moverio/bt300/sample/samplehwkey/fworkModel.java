package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 4/6/2018.
 */

public class fworkModel implements Serializable {
    private String c_id;
    private String c_code;
    private String c_dscr;
    private String c_first_event;

    public fworkModel(String id, String c_code, String c_dscr, String first_event) {
        this.c_id = id;
        this.c_code = c_code;
        this.c_dscr = c_dscr;
        this.c_first_event = first_event;
    }

    public String getC_Id() { return c_id; }

    public String getC_code() { return c_code; }

    public String getC_dscr() { return c_dscr; }

    public String getFirst_event() { return c_first_event; }

    public void setC_id(String c_id) { this.c_id = c_id; }

    public void setC_code(String c_code) { this.c_code = c_code; }

    public void setC_dscr(String c_dscr) { this.c_dscr = c_dscr; }

    public void setC_first_event(String c_first_event) { this.c_first_event = c_first_event; }
}
