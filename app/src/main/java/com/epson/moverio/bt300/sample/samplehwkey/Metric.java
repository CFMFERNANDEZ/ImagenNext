package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class Metric implements Serializable {

    private String measure_dscr;
    private String measure_code;
    private String measure_htarget;
    private String measure_ltarget;

    public Metric(String code, String dscr, String htarget, String ltarget){
        this.measure_dscr = dscr;
        this.measure_code = code;
        this.measure_htarget = htarget;
        this.measure_ltarget = ltarget;
    }

    public String getMeasure_dscr() {
        return measure_dscr;
    }

    public String getMeasure_code() {
        return measure_code;
    }

    public String getMeasure_htarget() {
        return measure_htarget;
    }

    public String getMeasure_ltarget() {
        return measure_ltarget;
    }
}
