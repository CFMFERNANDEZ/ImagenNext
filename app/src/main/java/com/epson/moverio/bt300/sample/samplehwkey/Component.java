package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class Component {

    private String code;
    private String dscr;
    private int quantity;
    private String image;

    public Component(String code, String dscr, int quantity, String image){
        this.code = code;
        this.dscr = dscr;
        this.quantity = quantity;
        this.image = image;
    }

    public String getCode(){
        return this.code;
    }

    public String getDscr(){
        return this.dscr;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public String getImage(){
        return this.image;
    }
}
