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
    //Query WSMD by table and ID
    @Headers({
            "database: PLW-QAS-S"
    })
    @GET("querytable/WSMD/bycode/{id}")
    Call<List<WSMDmodel>> getWSMD(@Path("id") String id);
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

    //Get the user image
    @Headers({
            "Lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("image/unsupervised/personnel/{id}")
    Call<List<OrdersModel>> getUserImage(@Path("id") String id);

    //Get fworks of a order
    //Get the user image
    @Headers({
            "Lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("mfgseqorder/oms/{id}")
    Call<List<fworkModel>> getfWorks(@Path("id") String id);

    //
    @Headers({
            "Lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("mfgseqorder/next/{mfseq_id}/{fwork_id}")
    Call<List<String>> nextOMS(@Path("mfseq_id") String mfseq_id, @Path("fwork_id") String fwork_id );


    //Get IMAGE by Path
    @Headers({
            "lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("image/{path}")
    Call<List<Image>> getImageByPath(@Path("path") String path);

}
