package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;

public class Issue implements Serializable {

    private String desc;
    private String id;

    public Issue(String dsrc, String id){
        this.desc = dsrc;
        this.id = id;
    }

    public String getDscr() { return desc; }

    public void setDscr(String dscr) { this.desc = dscr; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
