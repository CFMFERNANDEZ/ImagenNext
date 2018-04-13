/*
 * Copyright(C) Seiko Epson Corporation 2016. All rights reserved.
 *
 * Warranty Disclaimers.
 * You acknowledge and agree that the use of the software is at your own risk.
 * The software is provided "as is" and without any warranty of any kind.
 * Epson and its licensors do not and cannot warrant the performance or results
 * you may obtain by using the software.
 * Epson and its licensors make no warranties, express or implied, as to non-infringement,
 * merchantability or fitness for any particular purpose.
 */

package com.epson.moverio.bt300.sample.samplehwkey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OMSDisplayActivity extends AppCompatActivity implements SpeechRecognizerManager.OnResultListener {

    private ImageView mImageView;
    private View mContentView;
    private ListView listViewComponent;
    private ListView listViewMetric;
    private int mImageIndex;
    private OrdersModel order;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private AlertDialog alertComponent;
    private AlertDialog alertMetric;
    private AlertDialog tpcDialog;
    private View componentView;
    private View metricView;
    private View tpcView;
    private ArrayList<Component> components;
    private ArrayList<Metric> metrics;
    private fworkModel fworkActual;
    private fworkModelList fworks;
    private APIService apiService;
    private HashMap<String, Image> mapImages;
    private String mfseqId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        mContentView = findViewById(R.id.oms_image);
        setImmersive();
        mfseqId = getIntent().getStringExtra("mfseq_id");
        order = (OrdersModel) getIntent().getSerializableExtra("order");
        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
        mImageView = (ImageView) findViewById(R.id.oms_image);
        fworks = (fworkModelList) getIntent().getSerializableExtra("fworkList");
        int i = 0;
        for(fworkModel o : fworks.getList()){
            if(o.getFirst_event()!=null){
                if(o.getFirst_event().equals("true")){
                    fworkActual = o;
                }
            }
            Log.d("FWORK BEGIN", o.getC_Id());
        }
        mapImages = new HashMap<>();
        if(fworkActual == null){
            fworkActual = fworks.getList().get(0);
            fworkActual.setC_first_event("true");
        }
        updateByFwork();
    }

    public void setImmersive(){
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        for(String command:commands)
        {
            if (command.toLowerCase().contains("open") || command.toLowerCase().contains("show") ){
                if (command.toLowerCase().contains("metric")){
                    alertMetric.show();
                }else if(command.toLowerCase().contains("component")){
                    alertComponent.show();
                }
            }else if (command.toLowerCase().contains("next")){
                mImageIndex++;
                new nextOms().execute();
                updateByFwork();
            }else  if (command.toLowerCase().contains("back") || command.toLowerCase().contains("previuos")){
                mImageIndex--;
                updateByFwork();
            }else  if (command.toLowerCase().contains("confirm") || command.toLowerCase().contains("PC") ||
                    command.toLowerCase().contains("end") || command.toLowerCase().contains("pipc")){
                tpcDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("mfseqid", mfseqId);
                setResult(RESULT_OK, intent);
                super.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageIndex = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    mImageIndex--;
                    updateByFwork();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mImageIndex++;
                    new nextOms().execute();
                    updateByFwork( );
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSpeechRecognizerManager = new SpeechRecognizerManager(this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    alertComponent.show();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public class nextOms extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.181");
            final String url = "http://"+ server + ":8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            final Call<List<String>> nextOMSResponse = apiService.nextOMS(order.getId(),fworkActual.getC_Id());  //Return success

            nextOMSResponse.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if(response.isSuccessful() && response.body().size() > 0) {
                        Log.d("SUCCESS",response+"");
                         if(mImageIndex != fworks.getList().size()) {
                             fworkActual.setC_first_event("true");
                        }else{
                            TPCDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Log.e("Error", t+"");
                }
            });
            return null;
        }
    }

    private void TPCDialog(){
         /*TPC DIALOG*/
        AlertDialog.Builder tpcBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater tpcInflater = LayoutInflater.from(getApplicationContext());
        tpcView = tpcInflater.inflate(R.layout.tpc_alert_layout, null);
        TextView tpcCode = (TextView)tpcView.findViewById(R.id.tpc_ordercode);
        tpcCode.setText(order.getAsm_code());
        TextView tpcDscr = (TextView)tpcView.findViewById(R.id.tpc_orderdscr);
        tpcDscr.setText(order.getAsm_dscr());
        TextView tpcMessage = (TextView)tpcView.findViewById(R.id.tpc_message);
        tpcMessage.setText("Confirm your TPC.");
        tpcBuilder.setView(tpcView);
        tpcBuilder.setTitle("TPC Confirmation.");
        tpcBuilder.setIcon(R.drawable.ok);
        tpcBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tpcDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("mfseqid", mfseqId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        tpcBuilder.setCancelable(false);
        tpcDialog = tpcBuilder.create();
        tpcDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(OMSDisplayActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(OMSDisplayActivity.this);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        tpcDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizerManager.destroy();
    }

    public class omsPicture extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.181");
            final String url = "http://"+ server + ":8080/WebServicesCellFusion/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(APIService.class);
            String pathCoded = Base64.encodeToString( fworkActual.getOms_path().getBytes(), Base64.NO_WRAP);
            Call<List<Image>> image = apiService.getImageByPath(pathCoded);  //Return one record searching by CODE
            image.enqueue(new Callback<List<Image>>() {
                @Override
                public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        Image image = response.body().get(0);
                        mImageView = (ImageView)findViewById(R.id.oms_image);
                        mImageView.setImageBitmap(image.getImage());
                        mapImages.put(fworkActual.getC_Id(), image);
                    }else{
                        Toast.makeText(getApplicationContext(), "Cant load image", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<List<Image>> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
            return null;
        }
    }

    public void updateByFwork(){
        //Update image
        if(mImageIndex < fworks.getList().size()){
            fworkActual = fworks.getList().get(mImageIndex);
            Log.d("FWORK_IMAGE", fworkActual.getC_Id()+"__"+fworkActual.getOms_path());
            Image image = mapImages.get(fworkActual.getC_Id());
            if( image == null ){
                new omsPicture().execute();
            }else{
                mImageView = (ImageView)findViewById(R.id.oms_image);
                mImageView.setImageBitmap(image.getImage());
            }
            //Update metrics
            createMetricAlert();
            //Update Component
            createComponentAlert();
        }
    }

    public void createComponentAlert(){
        ComponentListAdapter adapterList = new ComponentListAdapter(this, fworkActual.getComponent());
        AlertDialog.Builder builder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        componentView = inflater.inflate(R.layout.component_alert_layout, null);
        listViewComponent = (ListView)componentView.findViewById(R.id.componente_list);
        listViewComponent.setAdapter(adapterList);
        builder.setView(componentView);
        builder.setTitle("COMPONENT LIST");
        builder.setIcon(R.drawable.componentlist);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        builder.setCancelable(false);
        alertComponent = builder.create();
    }

    public void createMetricAlert(){
        MetricListAdapter metricAdapter = new MetricListAdapter(this, fworkActual.getMeasures());
        AlertDialog.Builder metricBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater metricInflater = LayoutInflater.from(getApplicationContext());
        metricView = metricInflater.inflate(R.layout.metrics_alert_layout, null);
        listViewMetric = (ListView)metricView.findViewById(R.id.metrics_list);
        listViewMetric.setAdapter(metricAdapter);
        metricBuilder.setView(metricView);
        metricBuilder.setTitle("Metric list");
        metricBuilder.setIcon(R.drawable.list_icon);
        metricBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        metricBuilder.setCancelable(false);
        alertMetric = metricBuilder.create();
    }
}
