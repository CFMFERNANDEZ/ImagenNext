package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class Component implements Serializable {

    private String comp_code;
    private String comp_dscr;
    private int comp_qty;
    private String comp_tracking;
    private String trackingInput;

    public Component(String code, String dscr, int quantity, String comp_tracking) {
        this.comp_code = code;
        this.comp_dscr = dscr;
        this.comp_qty = quantity;
        this.comp_tracking = comp_tracking;
    }

    public String getCode() {
        return this.comp_code;
    }

    public String getDscr() {
        return this.comp_dscr;
    }

    public int getQuantity() {
        return this.comp_qty;
    }

    public String getComp_tracking(){ return this.comp_tracking; }

    public String gettrackingInput(){
        return this.trackingInput;
    }

    public void settrackingInput( String input){
        this.trackingInput = input;
    }
}
