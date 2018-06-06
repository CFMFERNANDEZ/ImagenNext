package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/28/2018.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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

    //Service for reportTQC
    @POST("tqcservice/tqcasm")
    Call<List<ReportTqcResponse>> reportTQC(@Body TQCasm tqc);


    //Get available issues
    @Headers({
            "lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("availissues/asm")
    Call<List<Issue>> getAvailIssues();

    //Get priority
    @Headers({
            "lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("ctbprovider/CTB9032")
    Call<List<priorities>> getPriority();

    //Get defect provider
    @Headers({
            "lang: EN",
            "database: PLW-QAS-S"
    })
    @GET("defectprovider/tqcasm/{mfseqorder}/{fwork}/{personnel}")
    Call<List<Defects>> getDefects(@Path("mfseqorder") String mfseqorder, @Path("fwork") String fwork, @Path("personnel") String personnel);

    //Service for asign Image to Record
    @POST("image/imgagn")
    Call<List<SimpleResponse>> asignImage(@Body FullImage fullImage);
}
