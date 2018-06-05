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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


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
    private Spinner spinnerPrio;
    private Spinner spinnerDefect;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private TextView textOrd;
    private TextView textWorkS;
    private EditText textComments;

    private String mfseqOrder;
    private String WS;
    private fworkModel fWork;
    private Bitmap bitMap;
    private Personnel actualPerson;
    private String comments="";

    private List<Issue> auxIssues = new ArrayList<>();
    private List<priorities> auxPrio = new ArrayList<>();
    private List<Defects> auxDefects = new ArrayList<>();

    private boolean comment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tqc);

        mfseqOrder = getIntent().getStringExtra("MfseqOrder");
        fWork = (fworkModel)getIntent().getSerializableExtra("Fwork");
        actualPerson = (Personnel) getIntent().getSerializableExtra("Person");
        Log.d("INSIDEREPORT", mfseqOrder + "-" + fWork);
        fworkActual =(fworkModel) getIntent().getSerializableExtra("Fwork");
        mfseqId = getIntent().getStringExtra("mfseqId");
        WS = getIntent().getStringExtra("WS");
        //mContentView = findViewById(R.id.oms_image);
        //setImmersive();
        //createReport();
        new availIssues().execute();
        new priority().execute();
        new defects().execute();

        textOrd = (TextView) findViewById(R.id.textOrder);
        textWorkS = (TextView) findViewById(R.id.textWS);

        textOrd.setText(mfseqId);
        textWorkS.setText(WS);
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
        }
    }
    public void clickReport(View view){
        new reportTQC().execute();
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
                        auxIssues = response.body();
                        spinner = (Spinner) findViewById(R.id.spinnerAvailIssue);
                        ArrayList<String> Issues = new ArrayList<String>();
                        int i = 0;
                        for(Issue issue: response.body()){
                            Issues.add(issue.getDscr());
                            i++;
                        }
                        Log.d("AvailIssues",Issues.toString()+"");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, Issues);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);;
                        spinner.setAdapter(adapter);
                        spinner.setSelection(3);
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

    public class priority extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.166");
            final String url = "http://" + server + ":8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(APIService.class);
            Call<List<priorities>> prio = apiService.getPriority();
            prio.enqueue(new Callback<List<priorities>>() {
                @Override
                public void onResponse(Call<List<priorities>> call, Response<List<priorities>> response) {
                    if(response.isSuccessful() && response.body().size() > 0){
                        auxPrio = response.body();
                        spinnerPrio = (Spinner) findViewById(R.id.spinnepriority);
                        ArrayList<String> prios = new ArrayList<String>();
                        int i = 0;
                        for(priorities p: response.body()){
                            prios.add(p.getC_dscr());
                            i++;
                        }
                        Log.d("AvailIssues",prios.toString()+"");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, prios);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);;
                        spinnerPrio.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<priorities>> call, Throwable t) {

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

            String auxIdPriority="";
            String auxIdDefect="";
            String auxIdAvailIssue="";
            int i = 0;
            for(priorities p: auxPrio){
                if(i == spinnerPrio.getSelectedItemPosition() ){
                    auxIdPriority = p.getC_id().toString();
                    i = 0;
                    break;
                }
                i++;
            }
            for(Defects d: auxDefects){
                if(i == spinnerDefect.getSelectedItemPosition()){
                    auxIdDefect = d.getC_id().toString();
                    i=0;
                    break;
                }
                i++;
            }
            for(Issue issue : auxIssues){
                if(i == spinner.getSelectedItemPosition()){
                    auxIdAvailIssue = issue.getId().toString();
                    i=0;
                    break;
                }
                i++;
            }

            EditText auxComments =(EditText) findViewById(R.id.comments);
            comments = auxComments.getText().toString();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //TQCasm tqc = new TQCasm("PLW-QAS-S","EN", encoded, fWork.getC_Id(), mfseqOrder, actualPerson.getC_id(), "[AvailIssues:2001]" );
            TQCasm tqc = new TQCasm("PLW-QAS-S","EN", encoded, fWork.getC_Id(), mfseqOrder, actualPerson.getC_id(), auxIdAvailIssue,auxIdPriority,auxIdDefect, comments );
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

    public class defects extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.166");
            final String url = "http://" + server + ":8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(APIService.class);
            Call<List<Defects>> def = apiService.getDefects(mfseqOrder,fworkActual.getC_Id(),actualPerson.getC_id());
            def.enqueue(new Callback<List<Defects>>() {
                @Override
                public void onResponse(Call<List<Defects>> call, Response<List<Defects>> response) {
                    if(response.isSuccessful() && response.body().size() > 0){
                        auxDefects = response.body();
                        spinnerDefect = (Spinner) findViewById(R.id.spinnerdefect);
                        ArrayList<String> defs = new ArrayList<String>();
                        int i = 0;
                        for(Defects d: response.body()){
                            defs.add(d.getC_dscr());
                            i++;
                        }
                        Log.d("AvailIssues",defs.toString()+"");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, defs);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinnerDefect.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Defects>> call, Throwable t) {

                }
            });
            return null;
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        if(!comment) {
            for (String command : commands) {
                if (command.toLowerCase().contains("picture")) {
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
                }
                if (command.toLowerCase().contains("defect")) {
                    spinnerDefect.performClick();
                    Log.d("Focus", spinner + "");
                    Log.d("Focus", spinnerDefect + "");
                    Log.d("Focus", spinnerPrio + "");
                    Log.d("Focus", "");
                }
                if (command.toLowerCase().contains("decision")) {
                    spinner.performClick();
                }
                if (command.toLowerCase().contains("priority")) {
                    spinnerPrio.performClick();
                }
                if (command.toLowerCase().contains("comments")) {
                    EditText editText = findViewById(R.id.comments);

                    editText.requestFocus();

                    mSpeechRecognizerManager = new SpeechRecognizerManager(this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
                    comment = true;
                }
            }
        }else{
            EditText editText = findViewById(R.id.comments);
            editText.requestFocus();

            String aux = editText.getText().toString();
            if(commands.get(0).toLowerCase().contains("finish")){
                comment = false;
            }else {
                aux += " " + commands.get(0).toLowerCase();
                editText.setText("");
                editText.setText(aux);
            }
        }
    }

}
