package com.epson.moverio.bt300.sample.samplehwkey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectionActivity extends Activity implements SpeechRecognizerManager.OnResultListener, ZXingScannerView.ResultHandler{

    /**
    * OUR DATA AND VARS-----------------------------------------------------------------------
     * * * * */
    private static final int ALL_PERMISSION = 1;

    private View mContentView;
    private ListView listView;
    private TextView wsmdText;
    private TextView personText;
    private static final String TAG = "MyStt3Activity";
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private static final int CAMERA_PIC_REQUEST = 1337;
    ArrayList<OrdersModel> values = new ArrayList();
    private boolean mVisible;
    private String orderInAction;
    private ZXingScannerView mScannerView;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};
    private WSMDmodel WSMD;
    private Personnel actualPerson;

    public static String ID;
    public static OrdersModel auxOrderModel;

    public static String IdOms;

    private static String font_path = "font/EUEXCF.TTF";
    private static Typeface TF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        TF = Typeface.createFromAsset(getAssets(),font_path);
        //Immersive mode
        mContentView = findViewById(R.id.WSLabel);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //HERE WE READ THE VALUE SENDED PREVIUSLY
        WSMD  = (WSMDmodel) getIntent().getSerializableExtra("WSMD");
        actualPerson  = (Personnel) getIntent().getSerializableExtra("Person");
        fillText();
        OrdersModelList orders = (OrdersModelList)getIntent().getSerializableExtra("Orders");
        values = new ArrayList();
        for(OrdersModel o : orders.getList()){
            values.add(new OrdersModel(o.getId(),o.getStatus_dscr(),o.getLotno(),o.getQty(),o.getAsm_code(),o.getAsm_dscr(),o.getEvent_pos(),o.getEvent_code()));

        }
        createMfSeqList();
    }

    public void fillText(){
        if( WSMD != null){
            wsmdText = (TextView)findViewById(R.id.WSLabel);
            wsmdText.setTypeface(TF);
            wsmdText.setText(WSMD.getC_code()+ "-"+WSMD.getC_dscr());
        }
        if(actualPerson != null){
            personText =( TextView)findViewById(R.id.selection_personname);
            personText.setTypeface(TF);
            personText.setText( actualPerson.getC_fname() +" "+ actualPerson.getC_lname() );
        }
    }

    public class fwork extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.166");
            Log.d("SERVER", server);
            final String url = "http://"+ server + ":8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            final Call<List<fworkModel>> fworks = apiService.getfWorks(ID);  //Return one record searching by CODE

            fworks.enqueue(new Callback<List<fworkModel>>() {
                @Override
                public void onResponse(Call<List<fworkModel>> call, Response<List<fworkModel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        List<fworkModel> auxList = new ArrayList();
                        int i = 0;
                        for(fworkModel fwModel : response.body()){
                            Log.d("IDFWORK", fwModel.getC_Id().toString()+"");
                            auxList.add(response.body().get(i));
                            i++;
                        }
                        Intent orderIntent = new Intent(getApplicationContext(), OMSDisplayActivity.class);
                        orderIntent.putExtra("order",auxOrderModel);
                        orderIntent.putExtra("fworkList", new fworkModelList(auxList));
                        orderIntent.putExtra("mfseq_id", ID);
                        startActivityForResult(orderIntent, 100);
                    }else{
                        Toast.makeText(getApplicationContext(), "Order not valid", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<List<fworkModel>> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
            return null;
        }
    }

    public void QrScanner(){
        Log.d("SELECTION","onScanner");
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        Log.d("ON PAUSE", "On Pause" );
        super.onPause();
        if( mSpeechRecognizerManager != null ){
            mSpeechRecognizerManager.destroy();
        }
        if( mScannerView != null ){
            mScannerView.stopCamera();
        }
    }

    @Override
    protected void onResume() {
        Log.d("ON RESUME", "On RESUME" );
        super.onResume();
        if(mSpeechRecognizerManager != null ) {
            mSpeechRecognizerManager = new SpeechRecognizerManager(this);
            mSpeechRecognizerManager.setOnResultListner(this);
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        OrdersModel odertoSend= null;
        for(String command:commands)
        {
            if( command.toLowerCase().contains("open")  || command.toLowerCase().contains("scann") || command.toLowerCase().contains("scan")  || command.toLowerCase().contains("scanner") ||
                    command.toLowerCase().contains("order") || command.toLowerCase().contains("orden")|| command.toLowerCase().contains("barcode")){
                QrScanner();
                break;
            }
        }
    }

    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();
        setContentView(R.layout.activity_selection);
        for(int i = 0 ; i < values.size(); i++){
            if( values.get(i).getLotno().equalsIgnoreCase(rawResult.getText())){
                setContentView(R.layout.activity_order_screen);
                OrdersModel item = values.get(i);
                Toast.makeText(getApplicationContext(), item.getAsm_dscr() + " selected", Toast.LENGTH_LONG).show();
                ID = item.getLotno();
                auxOrderModel = item;
                new fwork().execute();
            }
        }
        fillText();
        createMfSeqList();
        Toast.makeText(this, "Order not valid", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d("ON Destroy Main", "ON Destroy Main"); // Prints scan results
        super.onDestroy();
        if( mSpeechRecognizerManager != null ){
            mSpeechRecognizerManager.destroy();
        }
        if( mScannerView != null ){
            mScannerView.stopCamera();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSpeechRecognizerManager = new SpeechRecognizerManager(this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    Toast.makeText(getApplicationContext(), "Comand not valid", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                String mfseqid = data.getStringExtra("mfseqid");
                for( int i = 0; i < values.size(); i++){
                    if( values.get(i).getId().equalsIgnoreCase(mfseqid)){
                        values.remove(i);
                        createMfSeqList();
                    }
                }
            }
        }
    }

    public void createMfSeqList(){
        MfseqOrderAdapter adapter = new MfseqOrderAdapter(this, values);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrdersModel item = (OrdersModel) listView.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), item.getAsm_dscr() + " selected", Toast.LENGTH_LONG).show();
                ID = item.getLotno();
                auxOrderModel = item;
                new fwork().execute();
            }
        });
    }
}
