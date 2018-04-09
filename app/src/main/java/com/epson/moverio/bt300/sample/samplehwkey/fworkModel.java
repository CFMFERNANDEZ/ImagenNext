package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CellFusion on 4/6/2018.
 */

public class fworkModel implements Serializable {
    private String c_id;
    private String c_code;
    private String c_dscr;
    private String c_first_event;
    private String first_event;
    private String oms_path;
    private List<Component> components;
    private List<Metric> measures;

    public fworkModel(String id, String c_code, String c_dscr, String first_event, String oms_path ,List<Component> comps, List<Metric> measures) {
        this.c_id = id;
        this.c_code = c_code;
        this.c_dscr = c_dscr;
        this.c_first_event = first_event;
        this.first_event = first_event;
        this.oms_path = oms_path;
        this.components = comps;
        this.measures = measures;
    }

    public String getC_Id() { return c_id; }

    public String getC_code() { return c_code; }

    public String getC_dscr() { return c_dscr; }

    public void setC_id(String c_id) { this.c_id = c_id; }

    public void setC_code(String c_code) { this.c_code = c_code; }

    public void setC_dscr(String c_dscr) { this.c_dscr = c_dscr; }

    public void setC_first_event(String c_first_event) { this.c_first_event = c_first_event; }
    public String getFirst_event() { return first_event; }

    public String getOms_path() {
        return this.oms_path;
    }

    public List<Component> getComponent() {
        return components;
    }

    public List<Metric> getMeasures() {
        return measures;
    }
}
