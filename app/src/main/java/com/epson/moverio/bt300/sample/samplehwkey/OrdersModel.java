package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/29/2018.
 */
import java.io.Serializable;
import java.util.List;

public class OrdersModel implements Serializable {
    private String id;
    private String status_dscr;
    private String lotno;
    private String qty;
    private String asm_code;
    private String asm_dscr;
    private final int[] IMAGES = {
            R.drawable.i7935,
            R.drawable.i7938,
            R.drawable.i7941,
            R.drawable.i7944,
            R.drawable.i7947,
    };

    public OrdersModel(String id, String status_dscr, String lotno, String qty, String asm_code, String asm_dscr) {
        this.id = id;
        this.status_dscr = status_dscr;
        this.lotno = lotno;
        this.qty = qty;
        this.asm_code = asm_code;
        this.asm_dscr = asm_dscr;
    }

    public String getId() {
        return id;
    }

    public String getStatus_dscr() {
        return status_dscr;
    }

    public String getLotno() {
        return lotno;
    }

    public String getQty() {
        return qty;
    }

    public String getAsm_code() {
        return asm_code;
    }

    public String getAsm_dscr() {
        return asm_dscr;
    }

    public int getImage(int index){
        return IMAGES[index];
    }

    public int getOMSSize(){
        return IMAGES.length;
    }
}