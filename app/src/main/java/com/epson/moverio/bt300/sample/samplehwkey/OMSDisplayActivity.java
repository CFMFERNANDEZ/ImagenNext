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
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OMSDisplayActivity extends AppCompatActivity implements SpeechRecognizerManager.OnResultListener {

    private ImageView mImageView;
    private int mImageIndex;
    private MfseqOrder order;
    private SpeechRecognizerManager mSpeechRecognizerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);


        order = (MfseqOrder)getIntent().getSerializableExtra("order");
        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
        mImageView = (ImageView) findViewById(R.id.oms_image);
        Toast.makeText(this, order.getAsmDscr(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        for(String command:commands)
        {
            Log.d("ON OMS DISPLAY", command);
            if (command.toLowerCase().contains("show")){
                if (command.toLowerCase().contains("me") && command.toLowerCase().contains("OMS")){
                    if( command.toLowerCase().contains("cero") ){
                        mImageIndex = 0;
                        changeImage();
                    }else if( command.toLowerCase().contains("one") ){
                        mImageIndex = 1;
                        changeImage();
                    }else if( command.toLowerCase().contains("two") ||command.toLowerCase().contains("to")){
                        mImageIndex = 2;
                        changeImage();
                    }else if( command.toLowerCase().contains("three") ||command.toLowerCase().contains("tree")){
                        mImageIndex = 3;
                        changeImage();
                    }else if( command.toLowerCase().contains("four") ){
                        mImageIndex = 4;
                        changeImage();
                    }
                }
            }else if (command.toLowerCase().contains("next")){
                Log.d("ON NEXT", "ON NEXT");
                mImageIndex++;
                changeImage();
            }else  if (command.toLowerCase().contains("back") || command.toLowerCase().contains("previuos")){
                Log.d("ON BACK", "ON BACK");

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
                default:
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void changeImage() {
        Log.d("ON CHANGE IMAGE", mImageIndex+"");
        if (order.getOMSSize() <= mImageIndex) {
            mImageIndex = 0;
        } else if (mImageIndex < 0) {
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
