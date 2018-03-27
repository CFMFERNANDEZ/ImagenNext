package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/27/2018.
 */

public class Personnel {

    private String fName;
    private String lName;
    private String code;
    private String userCode;
    private String photo;

    public Personnel(String fname, String lname, String userCode, String photo, String code){
        this.fName = fname;
        this.lName = lname;
        this.userCode = userCode;
        this.photo = photo;
        this.code = code;
    }

    public String getfName(){
        return this.fName;
    }

    public String getlName(){
        return this.lName;
    }

    public String getUserCode(){
        return this.userCode;
    }

    public String getCode(){
        return this.code;
    }

    public String getPhoto(){
        return this.photo;
    }


}
