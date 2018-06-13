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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.zxing.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OMSDisplayActivity extends AppCompatActivity implements SpeechRecognizerManager.OnResultListener, ZXingScannerView.ResultHandler{

    private ImageView mImageView;
    private TextView steps;
    private View mContentView;
    private ListView listViewComponent;
    private ListView listViewMetric;
    private ListView listViewCheckMetric;
    private ListView listViewTracking;
    private VideoView videoms;
    private int mImageIndex;
    private OrdersModel order;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private AlertDialog alertComponent;
    private AlertDialog alertMetric;
    private AlertDialog alertCheckMetric;
    private AlertDialog tpcDialog;
    private AlertDialog trackingAlert;
    private AlertDialog reportTQC;
    private AlertDialog alertVideo;
    private View componentView;
    private View metricView;
    private View checkMetricView;
    private View tpcView;
    private View trackingView;
    private ArrayList<Component> components;
    private ArrayList<Metric> metrics;
    private List<Metric> checkMetrics;
    private int checkCounter = 0;
    private fworkModel fworkActual;
    private fworkModelList fworks;
    private APIService apiService;
    private HashMap<String, Image> mapImages;
    private String mfseqId;
    private String WS;
    private String mfseqorder_Id;
    private View iconMet;
    private View iconMat;
    private View iconIssue;
    private ZXingScannerView mScannerView;
    private Boolean metricsShown = false;
    private Boolean checkMetricsShown = false;
    private Boolean showTracking = false;

    private Boolean isTrackinkVisible = false;
    private TrackingListAdapter trackingAdapter;
    private MetricListAdapter metricListAdapter;
    private CheckListAdapter checkListAdapter;

    private Personnel actualPerson;

    private static String font_path = "font/EUEXCF.TTF";
    private static Typeface TF;

    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        //iconIssue.findViewById(R.id.issue);
        //iconIssue.setVisibility(View.VISIBLE);

        mContentView = findViewById(R.id.oms_image);
        setImmersive();
        mfseqId = getIntent().getStringExtra("mfseq_id");
        WS = getIntent().getStringExtra("WS");
        mfseqorder_Id = getIntent().getStringExtra("mfseqorder_id");
        order = (OrdersModel) getIntent().getSerializableExtra("order");
        actualPerson = (Personnel) getIntent().getSerializableExtra("Person");
        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
        mImageView = (ImageView) findViewById(R.id.oms_image);
        fworks = (fworkModelList) getIntent().getSerializableExtra("fworkList");
        int i = 0;
        mImageIndex = 0;
        for(fworkModel o : fworks.getList()){
            if(o.getFirst_event()!=null){
                if( o.getFirst_event().equals("true")){
//                    fworkActual = o;
                    mImageIndex = i;
                }
            }
            i++;
        }
        mapImages = new HashMap<>();
//        if(fworkActual == null){
//            mImageIndex = 0;
////            fworkActual = fworks.getList().get(0);
////            fworkActual.setC_first_event("true");
//        }
        updateByFwork();

        getSupportActionBar().hide();

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
        getSupportActionBar().hide();
        setImmersive();
        ((ImageView)findViewById(R.id.animated_voice)).setImageResource(R.drawable.m2);
        for(String command:commands)
        {
            if (command.toLowerCase().contains("open") || command.toLowerCase().contains("show") ){
                if (command.toLowerCase().contains("metric")){
                    alertMetric.show();
                }else if(command.toLowerCase().contains("component")){
                    alertComponent.show();
                }else if( command.toLowerCase().contains("scan")){
                    QrScanner();
                }
            }else if (command.toLowerCase().contains("close") || command.toLowerCase().contains("exit") ){
                if (command.toLowerCase().contains("metric")){
                    alertMetric.dismiss();
                    if(showTracking){
                        if(!trackingAdapter.canContinue()){
                            isTrackinkVisible = true;
                            trackingAlert.show();
                        }
                    }
                }else if(command.toLowerCase().contains("component")){
                    alertComponent.dismiss();
                }else if(command.toLowerCase().contains("tracking")){
                    if(trackingAdapter.canContinue()){
                        trackingAlert.dismiss();
                        isTrackinkVisible = false;
                        setImmersive();
                        mImageIndex++;
                        new nextOms().execute();
                        updateByFwork( );
                        metricsShown = false;
                    }
                }
            }else if (command.toLowerCase().contains("next")){
                if( !isTrackinkVisible){
                    metricsShown = true;
                    checkMetricsShown = true;
                    if(fworkActual.getMeasures()!= null && fworkActual.getMeasures().size() > 0){
                        List<Metric> auxList =  fworkActual.getMeasures();
                        for(Metric m : auxList){
                            if (m.getMeasureInput() == null || m.getMeasureInput() ==""){
                                metricsShown = false;
                                alertMetric.show();
                                metricListAdapter.resetInput();
                            }else if(m.getMeasureInput() != null || m.getMeasureInput() !=""){
                                if(Float.parseFloat(m.getMeasureInput().toString()) >= Float.parseFloat(m.getMeasure_ltarget()) && Float.parseFloat(m.getMeasureInput())<= Float.parseFloat(m.getMeasure_htarget())){
                                    metricsShown = true;
                                }else{
                                    metricsShown = false;
                                    alertMetric.show();
                                    metricListAdapter.resetInput();
                                }
                            }
                        }
                    }
                    //
                    if(fworkActual.getCheckMeasures()!= null && fworkActual.getCheckMeasures().size() > 0){
                        List<Metric> checkList =  checkListAdapter.getListMetrics();
                        for(Metric m : checkList){
                            if( !m.getCheck()){
                                alertCheckMetric.show();
                                checkMetricsShown = false;
                                break;
                            }
                        }
                    }
                    if( showTracking && metricsShown && checkMetricsShown ){
                        if(!trackingAdapter.canContinue()){
                            isTrackinkVisible = true;
                            trackingAlert.show();
                        }else{
                            showTracking = false;
                        }
                    }
                    if(!showTracking && (metricsShown && checkMetricsShown)){
                        mImageIndex++;
                        new nextOms().execute();
                        updateByFwork( );
                        metricsShown = false;
                        checkMetricsShown = false;
                    }
                }
                ////
            }else  if (command.toLowerCase().contains("back") || command.toLowerCase().contains("previuos")){
                if( !isTrackinkVisible){
                    mImageIndex--;
                    updateByFwork();
                }
            }else  if (command.toLowerCase().contains("confirm") || command.toLowerCase().contains("PC") ||
                    command.toLowerCase().contains("end") || command.toLowerCase().contains("pipc")){
                tpcDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("mfseqid", mfseqId);
                setResult(RESULT_OK, intent);
                super.finish();
            }else if( command.toLowerCase().contains("checked") || command.toLowerCase().contains("check") || command.toLowerCase().contains("cheque")  ){
                if( command.toLowerCase().contains("all") ){
                    if( checkMetrics != null && checkMetrics.size() > 0){
                        int i = 0;
                        for(Metric metric : checkMetrics ){
                            if(!checkMetrics.get(i).getCheck()){
                                checkListAdapter.setChecked(true, i);
                            }
                            i++;
                        }
                    }
                }else{
                    if( checkMetrics != null && checkMetrics.size() > 0){
                        if(!checkMetrics.get(checkCounter).getCheck()){
                            checkListAdapter.setChecked(true, checkCounter);
                            checkCounter++;
                        }else{
                            checkCounter++;
                        }
                    }
                }
                checkListAdapter.notifyDataSetChanged();
                break;
            }
            else if(command.toLowerCase().contains("report") || command.toLowerCase().contains("tqc")){
                //reportTQC.show();
                Intent intentReport = new Intent(getBaseContext(),ReportActivity.class);
                intentReport.putExtra("Fwork", fworkActual);
                intentReport.putExtra("MfseqOrder", mfseqorder_Id );
                intentReport.putExtra("Person", actualPerson);
                intentReport.putExtra("mfseqId",mfseqId);
                intentReport.putExtra("WS",WS);
                startActivity(intentReport);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    metricsShown = true;
                    checkMetricsShown = true;
                    if(fworkActual.getMeasures()!= null && fworkActual.getMeasures().size() > 0){
                        List<Metric> auxList =  fworkActual.getMeasures();
                        for(Metric m : auxList){
                            if (m.getMeasureInput() == null || m.getMeasureInput() ==""){
                                metricsShown = false;
                                alertMetric.show();
                                metricListAdapter.resetInput();
                            }else if(m.getMeasureInput() != null || m.getMeasureInput() !=""){
                                if(Float.parseFloat(m.getMeasureInput().toString()) >= Float.parseFloat(m.getMeasure_ltarget()) && Float.parseFloat(m.getMeasureInput())<= Float.parseFloat(m.getMeasure_htarget())){
                                    metricsShown = true;
                                }else{
                                    metricsShown = false;
                                    alertMetric.show();
                                    metricListAdapter.resetInput();
                                }
                            }
                        }
                    }
                    //
                    if(fworkActual.getCheckMeasures()!= null && fworkActual.getCheckMeasures().size() > 0){
                        List<Metric> checkList =  checkListAdapter.getListMetrics();
                        for(Metric m : checkList){
                            if( !m.getCheck()){
                                alertCheckMetric.show();
                                checkMetricsShown = false;
                                break;
                            }
                        }
                    }
                    if( showTracking && metricsShown && checkMetricsShown ){
                        if(!trackingAdapter.canContinue()){
                            isTrackinkVisible = true;
                            trackingAlert.show();
                        }else{
                            showTracking = false;
                        }
                    }
                    if(!showTracking && (metricsShown && checkMetricsShown)){
                        mImageIndex++;
                        new nextOms().execute();
                        updateByFwork( );
                        metricsShown = false;
                        checkMetricsShown = false;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSpeechRecognizerManager = new SpeechRecognizerManager(this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
                    ((ImageView)findViewById(R.id.animated_voice)).setImageResource(R.drawable.m1);
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
                        if(mImageIndex < fworks.getList().size()) {
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
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            tpcDialog.dismiss();
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
            TF = Typeface.createFromAsset(getAssets(),font_path);
            steps = (TextView)findViewById(R.id.steps);
            steps.setTypeface(TF);
            steps.setText((mImageIndex+1)+"/"+fworks.getList().size());

            fworkActual = fworks.getList().get(mImageIndex);
            Image image = mapImages.get(fworkActual.getC_Id());
            if( image == null ){
                new omsPicture().execute();
            }else{
                mImageView = (ImageView)findViewById(R.id.oms_image);
                mImageView.setImageBitmap(image.getImage());
            }
            //Update metrics
            createMetricAlert();
            //Update checklist
            createCheckListAlert();
            checkCounter = 0;
            //Update Component
            createComponentAlert();
            createReportAlert();
            createVideoAlert();
            //Update lotTRracking
            compTracking();

            if((fworkActual.getMeasures() != null && fworkActual.getMeasures().size() > 0) ||
                    (fworkActual.getCheckMeasures() != null && fworkActual.getCheckMeasures().size() > 0)){
                //alertMetric.show();
                iconMet = (ImageView) findViewById(R.id.iconMet);
                iconMet.setVisibility(View.VISIBLE);

                iconMet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( fworkActual.getMeasures() != null && fworkActual.getMeasures().size() > 0){
                            alertMetric.show();
                        }else if (fworkActual.getCheckMeasures() != null && fworkActual.getCheckMeasures().size() > 0){
                            alertCheckMetric.show();
                        }
                    }
                });
            }
            else {
                iconMet = (ImageView) findViewById(R.id.iconMet);
                iconMet.setVisibility(View.INVISIBLE);
            }

            if(fworkActual.getComponent() != null && fworkActual.getComponent().size() > 0){
                //alertMetric.show();
                iconMat = (ImageView) findViewById(R.id.iconMat);
                iconMat.setVisibility(View.VISIBLE);

                iconMat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertComponent.show();
                    }
                });
            }
            else {
                iconMat = (ImageView) findViewById(R.id.iconMat);
                iconMat.setVisibility(View.INVISIBLE);
            }

            if(fworkActual.getComponent() != null && fworkActual.getComponent().size() > 0){
                iconIssue = (ImageView) findViewById(R.id.issue);

                iconIssue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentReport = new Intent(getBaseContext(),ReportActivity.class);
                        intentReport.putExtra("Fwork", fworkActual);
                        intentReport.putExtra("MfseqOrder", mfseqorder_Id );
                        intentReport.putExtra("Person", actualPerson);
                        intentReport.putExtra("mfseqId",mfseqId);
                        intentReport.putExtra("WS",WS);
                        startActivity(intentReport);
                    }
                });
            }

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
        builder.setTitle("Material List");
        builder.setIcon(R.drawable.componentlist);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        builder.setCancelable(false);
        alertComponent = builder.create();
        alertComponent.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(OMSDisplayActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(OMSDisplayActivity.this);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            alertComponent.dismiss();
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void createMetricAlert(){
        metricListAdapter = new MetricListAdapter(this, fworkActual.getMeasures());
        AlertDialog.Builder metricBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater metricInflater = LayoutInflater.from(getApplicationContext());
        metricView = metricInflater.inflate(R.layout.metrics_alert_layout, null);
        listViewMetric = (ListView)metricView.findViewById(R.id.metrics_list);
        listViewMetric.setAdapter(metricListAdapter);
        metricBuilder.setView(metricView);
        metricBuilder.setTitle("Metric list");
        metricBuilder.setIcon(R.drawable.metriclist);
        metricBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
                if(fworkActual.getCheckMeasures() != null && fworkActual.getCheckMeasures().size() > 0){
                    alertCheckMetric.show();
                }else if(showTracking){
                    isTrackinkVisible=true;
                    trackingAlert.show();
                }
            }
        });
        metricBuilder.setCancelable(false);
        alertMetric = metricBuilder.create();
        alertMetric.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            alertMetric.dismiss();
                            createMetricAlert();
                            alertMetric.show();
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(OMSDisplayActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(OMSDisplayActivity.this);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            alertMetric.dismiss();
                            if(showTracking){
                                if(!trackingAdapter.canContinue()){
                                    isTrackinkVisible = true;
                                    trackingAlert.show();
                                }
                            }else{
                                ////////////
                                List<Metric> auxList =  fworkActual.getMeasures();
                                for(Metric m : auxList){
                                    if (m.getMeasureInput() == null || m.getMeasureInput() ==""){
                                        metricsShown = false;
                                        Toast.makeText( getApplicationContext(), "The measured value must be between the measurements " + m.getMeasure_ltarget() + " and " + m.getMeasure_htarget(), Toast.LENGTH_LONG).show();
                                        alertMetric.show();
                                        metricListAdapter.resetInput();
                                    }else if(m.getMeasureInput() != null || m.getMeasureInput() !=""){
                                        if(Float.parseFloat(m.getMeasureInput().toString()) >= Float.parseFloat(m.getMeasure_ltarget()) && Float.parseFloat(m.getMeasureInput())<= Float.parseFloat(m.getMeasure_htarget())){
                                            metricsShown = true;
                                        }else{
                                            metricsShown = false;
                                            alertMetric.show();
                                            Toast.makeText( getApplicationContext(), "The measured value must be between the measurements " + m.getMeasure_ltarget() + " and " + m.getMeasure_htarget(), Toast.LENGTH_LONG).show();
                                            metricListAdapter.resetInput();
                                        }
                                    }
                                }
                                checkMetricsShown = true;
                                if(fworkActual.getCheckMeasures()!= null && fworkActual.getCheckMeasures().size() > 0){
                                    List<Metric> checkList =  checkListAdapter.getListMetrics();
                                    for(Metric m : checkList){
                                        if( !m.getCheck()){
                                            alertCheckMetric.show();
                                            checkMetricsShown = false;
                                            break;
                                        }
                                    }
                                }
                                ////////////
                                if(metricsShown && checkMetricsShown){
                                    mImageIndex++;
                                    new nextOms().execute();
                                    updateByFwork( );
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void createReportAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        componentView = inflater.inflate(R.layout.report_tqc, null);
        builder.setView(componentView);
        builder.setTitle("Report TQC");
        builder.setIcon(R.drawable.componentlist);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        builder.setCancelable(false);
        reportTQC = builder.create();
        reportTQC.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:

                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:

                            break;
                        case KeyEvent.KEYCODE_DPAD_UP:

/*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                            Uri uriSavedImage = Uri.fromFile(image);

                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                            startActivityForResult(cameraIntent, 1);
*/
                            /**
                             * New URI form
                             */
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                Uri photoURI = null;
                                try {
                                    File imagesFolder = new File(Environment.getExternalStorageDirectory(),"Test");
                                    imagesFolder.mkdirs();
                                    File image = new File(imagesFolder, "foto.jpg");
                                    String path = image.getAbsolutePath();
                                    photoURI = FileProvider.getUriForFile(OMSDisplayActivity.this,
                                            getString(R.string.file_provider_authority),
                                            image);

                                } catch (Exception ex) {
                                    Log.e("TakePicture", ex.getMessage());
                                }
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, 1/*PHOTO_REQUEST_CODE*/);
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void createVideoAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        componentView = inflater.inflate(R.layout.video_layout, null);
        videoms = componentView.findViewById(R.id.videoOMS);

        videoms.setMediaController(new MediaController(this));
        videoms.setVideoURI(Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"));
        videoms.requestFocus();

        builder.setView(componentView);
        builder.setTitle("OMS video");
        builder.setIcon(R.drawable.componentlist);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        builder.setCancelable(false);
        alertVideo = builder.create();
        alertVideo.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:

                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            videoms.start();
                            break;
                        case KeyEvent.KEYCODE_DPAD_UP:

                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LLEGA","si llega");
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente almacenada en la memoria
            img=(ImageView)reportTQC.findViewById(R.id.imageReport);
            Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Test/"+"foto.jpg");
            //Añadimos el bitmap al imageView para mostrarlo por pantalla

            /**
             * Decision sugested
             *
             * Defects
             *
             * (Symptom reason)
             *
             */

            Log.d("Guardada","Foto");
            img.setImageBitmap(bMap);
        }
    }

    private void compTracking(){
        List<Component> components =fworkActual.getComponent();
        List<Component> compsTracking = new ArrayList<>();
        if( components != null && components.size() > 0){
            for(Component comp : components){
                if( comp.getComp_tracking() != null && !comp.getComp_tracking().equals("") && !comp.getComp_tracking().equals("L") && !comp.getComp_tracking().equals("V") ){
                    compsTracking.add(comp);
                    showTracking = true;
                }
            }
            trackingAdapter = new TrackingListAdapter(this, compsTracking);
            AlertDialog.Builder metricBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
            LayoutInflater metricInflater = LayoutInflater.from(getApplicationContext());

            trackingView = metricInflater.inflate(R.layout.tracking_alert_layout, null);

            listViewTracking = (ListView)trackingView.findViewById(R.id.tracking_list);
            listViewTracking.setAdapter(trackingAdapter);

            metricBuilder.setView(trackingView);
            metricBuilder.setTitle("Scan serial/ lot number");
            metricBuilder.setIcon(R.drawable.componentlist);
            metricBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setImmersive();
                    if(trackingAdapter.canContinue()){
                        mImageIndex++;
                        new nextOms().execute();
                        updateByFwork( );
                        metricsShown = false;
                    }
                }
            });
            metricBuilder.setCancelable(false);
            trackingAlert = metricBuilder.create();
            trackingAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                mSpeechRecognizerManager = new SpeechRecognizerManager(OMSDisplayActivity.this, true);
                                mSpeechRecognizerManager.setOnResultListner(OMSDisplayActivity.this);
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                QrScanner();
                                break;
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                if(trackingAdapter.canContinue()){
                                    trackingAlert.dismiss();
                                    isTrackinkVisible = false;
                                    setImmersive();
                                    mImageIndex++;
                                    new nextOms().execute();
                                    updateByFwork( );
                                    metricsShown = false;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }else{
            showTracking = false;
        }
    }

    public void createCheckListAlert(){
        checkMetrics =  fworkActual.getCheckMeasures();
        checkListAdapter = new CheckListAdapter(this,checkMetrics);
        AlertDialog.Builder checkListBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater metricInflater = LayoutInflater.from(getApplicationContext());
        checkMetricView = metricInflater.inflate(R.layout.checkmetrics_alert_layout, null);
        listViewCheckMetric = (ListView)checkMetricView.findViewById(R.id.checkmetrics_list);
        listViewCheckMetric.setAdapter(checkListAdapter);
        listViewCheckMetric.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLICKLIST","CKLICKLIST");
            }
        });
        checkListBuilder.setView(checkMetricView);
        checkListBuilder.setTitle("Metric list");
        checkListBuilder.setIcon(R.drawable.metriclist);
        checkListBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        checkListBuilder.setCancelable(false);
        alertCheckMetric = checkListBuilder.create();
        alertCheckMetric.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            alertCheckMetric.dismiss();
                            createMetricAlert();
                            alertCheckMetric.show();
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(OMSDisplayActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(OMSDisplayActivity.this);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            alertCheckMetric.dismiss();
                            if(showTracking){
                                if(!trackingAdapter.canContinue()){
                                    isTrackinkVisible = true;
                                    trackingAlert.show();
                                }
                            }else{
                                ////////////
                                List<Metric> auxList =  fworkActual.getMeasures();
                                for(Metric m : auxList){
                                    if (m.getMeasureInput() == null || m.getMeasureInput() ==""){
                                        metricsShown = false;
                                        Toast.makeText( getApplicationContext(), "The measured value must be between the measurements " + m.getMeasure_ltarget() + " and " + m.getMeasure_htarget(), Toast.LENGTH_LONG).show();
                                        alertMetric.show();
                                        metricListAdapter.resetInput();
                                    }else if(m.getMeasureInput() != null || m.getMeasureInput() !=""){
                                        if(Float.parseFloat(m.getMeasureInput().toString()) >= Float.parseFloat(m.getMeasure_ltarget()) && Float.parseFloat(m.getMeasureInput())<= Float.parseFloat(m.getMeasure_htarget())){
                                            metricsShown = true;
                                        }else{
                                            metricsShown = false;
                                            alertMetric.show();
                                            Toast.makeText( getApplicationContext(), "The measured value must be between the measurements " + m.getMeasure_ltarget() + " and " + m.getMeasure_htarget(), Toast.LENGTH_LONG).show();
                                            metricListAdapter.resetInput();
                                        }
                                    }
                                }
                                ////////////
                                if(metricsShown){
                                    mImageIndex++;
                                    new nextOms().execute();
                                    updateByFwork( );
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }


    public void QrScanner(){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        setContentView(mScannerView);
        trackingAlert.hide();
        try {
            mScannerView.startCamera(0);         // Start camera
        }catch (Exception e){
        }
    }

    public void handleResult(Result rawResult) {
        Log.d("OnResult","Result");
        trackingAlert.show();
        isTrackinkVisible = true;
        setContentView(R.layout.activity_order_screen);
        mScannerView.stopCamera();
        trackingAdapter.setTracking(rawResult.getText());
        trackingAdapter.notifyDataSetChanged();
    }
}
