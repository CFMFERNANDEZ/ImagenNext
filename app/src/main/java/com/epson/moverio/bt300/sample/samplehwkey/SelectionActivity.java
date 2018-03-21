package com.epson.moverio.bt300.sample.samplehwkey;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SelectionActivity extends Activity implements SpeechRecognizerManager.OnResultListener, ZXingScannerView.ResultHandler{

    /**
    * OUR DATA AND VARS-----------------------------------------------------------------------
     * * * * */
    private static final int ALL_PERMISSION = 1;

    private View mControlsView;
    private ListView listView;
    private static final String TAG = "MyStt3Activity";
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private static final int CAMERA_PIC_REQUEST = 1337;
    ArrayList<MfseqOrder> values = new ArrayList();
    private boolean mVisible;
    private ZXingScannerView mScannerView;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        values = new ArrayList();
        values.add(new MfseqOrder(1,"AND-FIGURE-001", "Android figure", "895221030116", "125606-001", "open"));
        values.add(new MfseqOrder(2,"ASM-DSCR-006", "ASM001", "237645", "125152-001", "open"));
        values.add(new MfseqOrder(3,"HP-TINT-954XL", "Tint for HP printer", "889296895213", "297RE-001", "open"));
        values.add(new MfseqOrder(4,"ASM-TEST-003", "ASM002", "45063", "48414-001", "closed"));
        values.add(new MfseqOrder(5,"HP-TINT-954XL", "Tint for HP printer", "889296895176", "192Y84-001", "open"));
        values.add(new MfseqOrder(6,"HP-TINT-954XL", "Tint for HP printer", "889296895213", "DPF385-001", "process"));

        MfseqOrderAdapter adapter = new MfseqOrderAdapter(this, values);
        Log.d("Context adapter",this.toString());
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MfseqOrder item = (MfseqOrder) listView.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), item.getAsmDscr() + " selected", Toast.LENGTH_LONG).show();
            }
        });
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (hasPermissions(this, PERMISSIONS)) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
                mSpeechRecognizerManager = new SpeechRecognizerManager(this);
                mSpeechRecognizerManager.setOnResultListner(this);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSION);
            }
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
        super.onResume();
        if(mSpeechRecognizerManager != null ) {
            mSpeechRecognizerManager = new SpeechRecognizerManager(this);
            mSpeechRecognizerManager.setOnResultListner(this);
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        MfseqOrder odertoSend= null;
        for(String command:commands)
        {
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
            if( values.get(i).getFlowId().equalsIgnoreCase(rawResult.getText())){
                MfseqOrder odertoSend = values.get(i);
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
}
