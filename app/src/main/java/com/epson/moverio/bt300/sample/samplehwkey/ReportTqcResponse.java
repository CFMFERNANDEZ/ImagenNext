package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 6/4/2018.
 */

public class ReportTqcResponse {
    public String status;
    public String repNumber;
    public String idRec;

    public ReportTqcResponse(String status, String repNumber, String idRec){
        this.status = status;
        this.repNumber = repNumber;
        this.idRec = idRec;
    }

    public String getIdRec() {
        return idRec;
    }

    public String getStatus(){
        return this.status;
    }

    public String getRepNumber() {
        return repNumber;
    }
}
