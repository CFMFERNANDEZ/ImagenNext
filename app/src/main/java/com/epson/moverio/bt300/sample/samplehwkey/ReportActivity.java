package com.epson.moverio.bt300.sample.samplehwkey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class ReportActivity extends AppCompatActivity {

    private AlertDialog reportTQC;
    private View mContentView;
    private View componentView;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tqc);
        //mContentView = findViewById(R.id.oms_image);
        //setImmersive();
        //createReport();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:

                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:

                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        Uri photoURI = null;
                        try {
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Test");
                            imagesFolder.mkdirs();
                            File image = new File(imagesFolder, "foto.jpg");
                            String path = image.getAbsolutePath();
                            photoURI = FileProvider.getUriForFile(ReportActivity.this,
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
        return super.dispatchKeyEvent(event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LLEGA","si llega");
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente almacenada en la memoria
            img=(ImageView)this.findViewById(R.id.imageReport);
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

}
