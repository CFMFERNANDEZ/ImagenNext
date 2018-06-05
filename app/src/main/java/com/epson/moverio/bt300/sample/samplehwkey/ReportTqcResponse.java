package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 6/4/2018.
 */

public class ReportTqcResponse {
    public String c_id;
    public String c_code;
    public String c_repno;

    public ReportTqcResponse(String c_id, String c_code, String c_repno){
        this.c_id = c_id;
        this.c_code = c_code;
        this.c_repno = c_repno;
    }

    public String getIdRec() {
        return c_id;
    }

    public String getCode(){
        return this.c_code;
    }

    public String getRepNumber() {
        return c_repno;
    }
}
