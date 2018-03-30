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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

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
    private View componentView;
    private View metricView;
    private ArrayList<Component> components;
    private ArrayList<Metric> metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        mContentView = findViewById(R.id.oms_image);
        setImmersive();

        order = (OrdersModel)getIntent().getSerializableExtra("order");
        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
        mImageView = (ImageView) findViewById(R.id.oms_image);

        /**
         * ALERT CONSTRUCTOR
         * */
        components = new ArrayList();
        components.add(new Component("00-001", "Component 1", 1, ""));
        components.add(new Component("00-002", "Component 2", 2, ""));
        components.add(new Component("00-003", "Component 3", 3, ""));
        components.add(new Component("00-004", "Component 4", 2, ""));
        components.add(new Component("00-005", "Component 5", 1, ""));
        components.add(new Component("00-006", "Component 6", 2, ""));

        ComponentListAdapter adapterList = new ComponentListAdapter(this, components);
        AlertDialog.Builder builder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        componentView = inflater.inflate(R.layout.component_alert_layout, null);
        listViewComponent = (ListView)componentView.findViewById(R.id.componente_list);
        listViewComponent.setAdapter(adapterList);
        builder.setView(componentView);
        builder.setTitle("Component list");
        builder.setIcon(R.drawable.i7938);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        builder.setCancelable(false);
        alertComponent = builder.create();

        /*ALERT METRICS*/
        metrics = new ArrayList();
        metrics.add(new Metric("radio", 15, 0));
        metrics.add(new Metric("resistance", 0.5, 0));
        metrics.add(new Metric("tolerance", 1, 0));
        metrics.add(new Metric("height", 5, 0));


        MetricListAdapter metricAdapter = new MetricListAdapter(this, metrics);
        AlertDialog.Builder metricBuilder = new AlertDialog.Builder(OMSDisplayActivity.this);
        LayoutInflater metricInflater = LayoutInflater.from(getApplicationContext());
        metricView = metricInflater.inflate(R.layout.metrics_alert_layout, null);
        listViewMetric = (ListView)metricView.findViewById(R.id.metrics_list);
        listViewMetric.setAdapter(metricAdapter);
        metricBuilder.setView(metricView);
        metricBuilder.setTitle("Metric list");
        metricBuilder.setIcon(R.drawable.i7938);
        metricBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImmersive();
            }
        });
        metricBuilder.setCancelable(false);
        alertMetric = metricBuilder.create();

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
                changeImage();
            }else  if (command.toLowerCase().contains("back") || command.toLowerCase().contains("previuos")){
                mImageIndex--;
                changeImage();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageIndex = 0;
        setImage(0);
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
                    changeImage();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mImageIndex++;
                    changeImage();
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

    private void changeImage() {
        Log.d("ON CHANGE IMAGE", mImageIndex+"");
        if (order.getOMSSize() <= mImageIndex) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Your Title");
            alertDialogBuilder.setMessage("Click yes to exit!").setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Toast.makeText(getApplicationContext(), "You have finish this production", Toast.LENGTH_LONG).show();
                            mImageIndex = 0;
                            setImmersive();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            mImageIndex = 0;
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        } else if (mImageIndex < 0) {
            Toast.makeText(getApplicationContext(), "You ARE IN THE BEGINING OF THE oms", Toast.LENGTH_LONG).show();
            mImageIndex = order.getOMSSize() - 1;
        }
        setImage(mImageIndex);
    }

    private void setImage(int index) {
        if (0 <= index && index < order.getOMSSize()) {
            mImageView.setImageResource(order.getImage(index));
            mImageIndex = index;
        }
    }
}
