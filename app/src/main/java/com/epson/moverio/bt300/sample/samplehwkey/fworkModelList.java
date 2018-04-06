package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CellFusion on 4/6/2018.
 */

public class fworkModelList implements Serializable {

    List<fworkModel> list;
    public fworkModelList(List<fworkModel> list) {
        this.list = list;
    }


    public List<fworkModel> getList() {
        return list;
    }
}
