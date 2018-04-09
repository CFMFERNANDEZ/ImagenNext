package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class Component implements Serializable{

    private String comp_code;
    private String comp_dscr;
    private int comp_qty;

    public Component(String code, String dscr, int quantity) {
        this.comp_code = code;
        this.comp_dscr = dscr;
        this.comp_qty = quantity;
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
}
