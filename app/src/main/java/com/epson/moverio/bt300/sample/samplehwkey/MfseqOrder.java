package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

/**
 * Created by CellFusion on 3/20/2018.
 */
@SuppressWarnings("serial")
public class MfseqOrder implements Serializable {
    private int secuence;
    private String asmDscr;
    private String asmCode;
    private String flowId;
    private String lotSerial;
    private String status;
    private final int[] IMAGES = {
            R.drawable.i7935,
            R.drawable.i7938,
            R.drawable.i7941,
            R.drawable.i7944,
            R.drawable.i7947,
    };

    public MfseqOrder( int secuence, String asmDscr, String asmCode, String flowId, String lotSerialN, String status){
        this.secuence = secuence;
        this.asmDscr = asmDscr;
        this.asmCode = asmCode;
        this.flowId = flowId;
        this.lotSerial = lotSerialN;
        this.status = status;
    }

    public String getAsmDscr(){
        return asmDscr;
    }

    public int getSecuence(){
        return secuence;
    }

    public String getAsmCode(){
        return asmCode;
    }

    public String getFlowId(){
        return flowId;
    }

    public String getLotSerial(){
        return lotSerial;
    }

    public String getStatus(){
        return status;
    }

    public int getImage(int index){
        return IMAGES[index];
    }

    public int getOMSSize(){
        return IMAGES.length;
    }

}
