package com.epson.moverio.bt300.sample.samplehwkey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
    private static final String TAG = "MyStt3Activity";
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private static final int CAMERA_PIC_REQUEST = 1337;
    ArrayList<OrdersModel> values = new ArrayList();
    private boolean mVisible;
    private String orderInAction;
    private ZXingScannerView mScannerView;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};

    public static String ID;
    public static OrdersModel auxOrderModel;

    public static String IdOms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        //Immersive mode
        mContentView = findViewById(R.id.WSLabel);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //HERE WE READ THE VALUE SENDED PREVIUSLY
        String WSMD  = getIntent().getStringExtra("WMSD");
        TextView WSLabel = (TextView) findViewById(R.id.WSLabel);
        WSLabel.setText("Workstation: "+WSMD);
        OrdersModelList orders = (OrdersModelList)getIntent().getSerializableExtra("Orders");
        Log.d("ORDENES", orders.getList().get(0).getAsm_code());
//        wsmdText = (TextView)findViewById(R.id.mfseq_wsmd);
//        wsmdText.setText(WSMD);
        Toast.makeText(getApplicationContext(), WSMD, Toast.LENGTH_LONG).show();


        values = new ArrayList();
        for(OrdersModel o : orders.getList()){
            values.add(new OrdersModel(o.getId(),o.getStatus_dscr(),o.getLotno(),o.getQty(),o.getAsm_code(),o.getAsm_dscr()));

        }
        /*values.add(new MfseqOrder(1,"AND-FIGURE-001", "Android figure", "895221030116", "125606-001", "open"));
        values.add(new MfseqOrder(2,"ASM-DSCR-006", "ASM001", "237645", "125152-001", "open"));
        values.add(new MfseqOrder(3,"HP-TINT-954XL", "Tint for HP printer", "889296895213", "297RE-001", "open"));
        values.add(new MfseqOrder(4,"ASM-TEST-003", "ASM002", "45063", "48414-001", "closed"));
        values.add(new MfseqOrder(5,"HP-TINT-954XL", "Tint for HP printer", "889296895176", "192Y84-001", "open"));
        values.add(new MfseqOrder(6,"HP-TINT-954XL", "Tint for HP printer", "889296895213", "DPF385-001", "process"));*/

        MfseqOrderAdapter adapter = new MfseqOrderAdapter(this, values);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrdersModel item = (OrdersModel) listView.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), item.getAsm_dscr() + " selected", Toast.LENGTH_LONG).show();
                ID = item.getId();
                auxOrderModel = item;
                new fwork().execute();
                //Intent orderIntent = new Intent(getApplicationContext(), OMSDisplayActivity.class);
                //orderIntent.putExtra("order", item);
                //orderInAction = "Estoy en Selection";
                //startActivity(orderIntent);
            }
        });
//        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
//        mSpeechRecognizerManager.setOnResultListner(this);

//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
//            if (hasPermissions(this, PERMISSIONS)) {
//                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
//                mSpeechRecognizerManager = new SpeechRecognizerManager(this);
//                mSpeechRecognizerManager.setOnResultListner(this);
//            } else {
//                ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSION);
//            }
//        }
    }

    public class fwork extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            final String url = "http://192.168.1.166:8080/WebServicesCellFusion/";

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
                        startActivity(orderIntent);
                    }else{
                        Toast.makeText(getApplicationContext(), "Personnel not valid", Toast.LENGTH_LONG).show();
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
        Log.d("Context QR",this.toString());
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
            Log.d("On googleRecResult", command);
            if (command.toLowerCase().contains("open")){
                if( command.toLowerCase().contains("order") || command.toLowerCase().contains("orden")){
                    String order = "";
                    if( command.toLowerCase().contains("cero") ){
                        order = "0";
                        odertoSend = values.get(0);
                    }else if( command.toLowerCase().contains("one") ){
                        order = "1";
                        odertoSend = values.get(1);
                    }else if( command.toLowerCase().contains("two") ||command.toLowerCase().contains("to")){
                        order = "2";
                        odertoSend = values.get(2);
                    }else if( command.toLowerCase().contains("three") ||command.toLowerCase().contains("tree")){
                        order = "3";
                        odertoSend = values.get(3);
                    }else if( command.toLowerCase().contains("four") ){
                        order = "4";
                        odertoSend = values.get(4);
                    }
                    Log.d("ON RESULT EXECUTION", odertoSend.toString());
                    if(odertoSend != null) {
                        Toast.makeText(this, "Do you want open the Order?" + order, Toast.LENGTH_SHORT).show();
                        Intent orderIntent = new Intent(this, OMSDisplayActivity.class);
                        orderIntent.putExtra("order", odertoSend);
                        startActivity(orderIntent);
                    }
                    return;
                }else if( command.toLowerCase().contains("camera") ){
//                    Toast.makeText(this,"You will open the camera", Toast.LENGTH_SHORT).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                    return;
                }else if( command.toLowerCase().contains("scann") || command.toLowerCase().contains("scan")  || command.toLowerCase().contains("scanner") ){
                    QrScanner();                    return;
                }
            }else if(command.toLowerCase().contains("scann") || command.toLowerCase().contains("scan")  || command.toLowerCase().contains("scanner")){
                if(command.toLowerCase().contains("order") || command.toLowerCase().contains("orden") || command.toLowerCase().contains("qr") || command.toLowerCase().contains("barcode")){
                    QrScanner();
                }
            }
        }
    }

    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();
        setContentView(R.layout.activity_order_screen);
        for(int i = 0 ; i < values.size(); i++){
            if( values.get(i).getId().equalsIgnoreCase(rawResult.getText())){
                OrdersModel odertoSend = values.get(i);
                Intent orderIntent = new Intent(this, OMSDisplayActivity.class);
                orderIntent.putExtra("order", odertoSend);
                startActivity(orderIntent);
            }
        }
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
}
