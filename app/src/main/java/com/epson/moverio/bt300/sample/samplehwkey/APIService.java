package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/28/2018.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface APIService {

    //Get user by code
    @Headers({
            "Lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("querytable/PERSONNEL/bycode/{code}")
    Call<List<Personnel>> getUsersByCode(@Path("code") String code);

    @GET("image/unsupervised/{table}/{id}")
    Call<List<Image>> getImage(@Path("table") String table, @Path("id") String id);
    //Get orders by WSMD
    @Headers({
            "Lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("mfgseqorder/ordersbywsmd/{wsmdid}")
    Call<List<OrdersModel>> getOrdersByWSMD(@Path("wsmdid") String wsmd);

}