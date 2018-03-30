package com.epson.moverio.bt300.sample.samplehwkey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CellFusion on 3/29/2018.
 */

public class OrdersModelList implements Serializable{
        List<OrdersModel> list;
        public OrdersModelList(List<OrdersModel> list) {
            this.list = list;
        }


    public List<OrdersModel> getList() {
        return list;
    }
}
