package com.epson.moverio.bt300.sample.samplehwkey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportActivity extends AppCompatActivity implements SpeechRecognizerManager.OnResultListener {

    private AlertDialog reportTQC;
    private View mContentView;
    private View componentView;
    private ImageView img;
    private fworkModel fworkActual;
    private String mfseqId;
    private APIService apiService;
    private Spinner spinner;
    private SpeechRecognizerManager mSpeechRecognizerManager;

    private String mfseqOrder;
    private fworkModel fWork;
    private Bitmap bitMap;
    private Personnel actualPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tqc);

        mfseqOrder = getIntent().getStringExtra("MfseqOrder");
        fWork = (fworkModel)getIntent().getSerializableExtra("Fwork");
        actualPerson = (Personnel) getIntent().getSerializableExtra("Person");
        Log.d("INSIDEREPORT", mfseqOrder + "-" + fWork);
        fworkActual =(fworkModel) getIntent().getSerializableExtra("Fwork");
        mfseqId = getIntent().getStringExtra("MfseqOrder");
        //mContentView = findViewById(R.id.oms_image);
        //setImmersive();
        //createReport();
        new availIssues().execute();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSpeechRecognizerManager = new SpeechRecognizerManager(this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
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
            bitMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Test/"+"foto.jpg");
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
            img.setImageBitmap(bitMap);
            new reportTQC().execute();
        }
    }


    public class availIssues extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.166");
            final String url = "http://" + server +":8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(APIService.class);
            Call<List<Issue>> availIssues = apiService.getAvailIssues();
            availIssues.enqueue(new Callback<List<Issue>>() {
                @Override
                public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                    if(response.isSuccessful() && response.body().size() > 0){
                        spinner = (Spinner) findViewById(R.id.spinnerAvailIssue);
                        ArrayList<String> Issues = new ArrayList<String>();
                        int i = 0;
                        for(Issue issue: response.body()){
                            Issues.add(issue.getDscr());
                            i++;
                        }
                        Log.d("AvailIssues",Issues.toString()+"");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, Issues);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
                        spinner.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Issue>> call, Throwable t) {
                    Log.d("Error","Un error a ocurrido al llenar la lista");
                }
            });
            return null;
        }

    }

    public class reportTQC extends AsyncTask<Void,Void,Void> {
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

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            TQCasm tqc = new TQCasm("PLW-QAS-S","EN", encoded, fWork.getC_Id(), mfseqOrder, actualPerson.getC_id(), "[AvailIssues:2001]" );
            final Call<TQCasm> response = apiService.reportTQC( tqc );  //Return success

            response.enqueue(new Callback<TQCasm>() {
                @Override
                public void onResponse(Call<TQCasm> call, Response<TQCasm> response) {
                    if(response.isSuccessful()) {
                        Log.d("SUCCESS",response+"");
                    }
                }
                @Override
                public void onFailure(Call<TQCasm> call, Throwable t) {
                    Log.e("Error", t+"");
                }
            });
            return null;
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        for(String command:commands)
        {
            if (command.toLowerCase().contains("Report") ) {

            }
        }
    }

}
