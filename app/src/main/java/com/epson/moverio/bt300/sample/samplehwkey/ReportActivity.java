package com.epson.moverio.bt300.sample.samplehwkey;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    private TextView spinnerIssues;
    private TextView spinnerPrio;
    private TextView spinnerDefect;

    private SpeechRecognizerManager mSpeechRecognizerManager;
    private TextView textOrd;
    private TextView textWorkS;
    private TextView labelDecision;
    private TextView labelPriority;
    private TextView labelDefect;
    private TextView labelws;
    private TextView labelOrder;
    private TextView labelComments;

    private EditText textComments;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    int notificationId = 1;
    int notificationProgress =0;
    int PROGRESS_MAX = 100;
    private  Timer t;
    private boolean sendedTQC = false;

    private boolean isReporting = false;

    private String mfseqOrder;
    private String WS;
    private fworkModel fWork;
    private Bitmap bitMap;
    private Bitmap resizedBitMap;
    private Personnel actualPerson;
    private String comments="";
    private ReportTqcResponse tqcResult;

    private List<Issue> auxIssues = new ArrayList<>();
    private List<priorities> auxPrio = new ArrayList<>();
    private List<Defects> auxDefects = new ArrayList<>();

    private AlertDialog alertIssues;
    private AlertDialog alertPrio;
    private AlertDialog alertDefect;
    private AlertDialog tqcReportedAlert;

    private TQCIssueAdapter issueAdapter;
    private TQCPrioAdapter prioAdapter;
    private TQCDefectAdapter defectAdapter;

    private ListView defectList;
    private ListView issueList;
    private ListView prioList;

    private Defects defectSelected;
    private priorities prioSelected;
    private Issue issueSelected;

    private boolean defectSelection = false;
    private boolean prioSelection = false;
    private boolean issueSelection = false;

    private boolean comment = false;
    private String font_path = "font/EUEXCF.TTF";
    private static Typeface TF;

    private boolean stateSpinner = false;
    private String spinnerSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_item);

        mfseqOrder = getIntent().getStringExtra("MfseqOrder");
        fWork = (fworkModel)getIntent().getSerializableExtra("Fwork");
        actualPerson = (Personnel) getIntent().getSerializableExtra("Person");
        fworkActual =(fworkModel) getIntent().getSerializableExtra("Fwork");
        mfseqId = getIntent().getStringExtra("mfseqId");
        WS = getIntent().getStringExtra("WS");
        //mContentView = findViewById(R.id.oms_image);
        //setImmersive();
        //createReport();
        new availIssues().execute();
        new priority().execute();
        new defects().execute();


        textOrd = findViewById(R.id.textOrder);
        textWorkS = findViewById(R.id.textWS);

         labelDecision = findViewById(R.id.decisionLabel);
         labelPriority = findViewById(R.id.priorityLabel);
         labelDefect = findViewById(R.id.defectLabel);
         labelws = findViewById(R.id.wsLabel);
         labelOrder = findViewById(R.id.orderLabel);
         labelComments = findViewById(R.id.commentsLabel);

        TF = Typeface.createFromAsset(getAssets(),font_path);

        textOrd.setTypeface(TF);
        textWorkS.setTypeface(TF);
        labelDecision.setTypeface(TF);
        labelPriority.setTypeface(TF);
        labelDefect.setTypeface(TF);
        labelws.setTypeface(TF);
        labelOrder.setTypeface(TF);
        labelComments.setTypeface(TF);
        textOrd.setText(mfseqId);
        textWorkS.setText(WS);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.qualityreport);
            actionBar.setTitle("Report TQC");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                    mSpeechRecognizerManager.setOnResultListner(this);
                    ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    Log.d("Entro","Click derecho");
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
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente almacenada en la memoria
            img=(ImageView)this.findViewById(R.id.imageReport);
            bitMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Test/"+"foto.jpg");
            double Height = Double.valueOf(bitMap.getHeight());
            double Width = Double.valueOf(bitMap.getWidth());
            double factor = 480/Height;
            double newWidth = factor*Width;
            double newHeight = factor* Height;
            resizedBitMap =  Bitmap.createScaledBitmap(bitMap, (int)newWidth, (int)newHeight, true);
            img.setImageBitmap(bitMap);
        }
    }
    public void clickReport(View view){
        sendedTQC = false;
            if( bitMap != null){
                Toast.makeText(getApplicationContext(), "Reporting TQC", Toast.LENGTH_LONG);
                new reportTQC().execute();
                Button b = findViewById(R.id.button);
                b.setText("Sending...");
                addNotification();
            }else{
                Toast.makeText(getApplicationContext(), "Please take a photo for the report", Toast.LENGTH_LONG);
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
                        auxIssues = response.body();
                        spinnerIssues = (TextView) findViewById(R.id.spinnerIssues);
                        spinnerIssues.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertIssues.show();

                            }
                        });
                        createIssuesDialog();
                        for( int i = 0; i < auxIssues.size(); i++){
                            if(auxIssues.get(i).getDscr().contains("ework")){
                                spinnerIssues.setText(auxIssues.get(i).getDscr());
                                issueSelected = auxIssues.get(i);
                                break;
                            }
                        }
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
                        spinnerPrio = (TextView) findViewById(R.id.spinnepriority);
                        spinnerPrio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertPrio.show();
                                prioSelection = true;
                            }

                        });
                        createPriorityDialog();
                        if(auxPrio.size() > 0){
                            spinnerPrio.setText(auxPrio.get(0).getC_dscr());
                            prioSelected = auxPrio.get(0);
                        }
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
            if(!isReporting){
                isReporting = true;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String server = prefs.getString("cf_server", "192.168.1.181");
                final String url = "http://"+ server + ":8080/WebServicesCellFusion/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                APIService apiService = retrofit.create(APIService.class);
                String auxIdPriority="";
                String auxIdDefect="";
                String auxIdAvailIssue="";
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                EditText auxComments =(EditText) findViewById(R.id.comments);
                comments = auxComments.getText().toString();
                TQCasm tqc = new TQCasm("PLW-QAS-S","EN", encoded, fWork.getC_Id(), mfseqOrder, actualPerson.getC_id(), issueSelected.getId(),prioSelected.getC_id(),defectSelected.getC_id(), comments );
                final Call<List<ReportTqcResponse>> response = apiService.reportTQC( tqc );  //Return success
                response.enqueue(new Callback<List<ReportTqcResponse>>() {
                    @Override
                    public void onResponse(Call<List<ReportTqcResponse>> call, Response<List<ReportTqcResponse>> response) {
                        if(response.isSuccessful()) {
                            if( response.body().size() > 0){
                                tqcResult = response.body().get(0);
                                new sendFullImage().execute();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ReportTqcResponse>> call, Throwable trow) {
                        if( !sendedTQC ){
                            sendedTQC = true;
                            isReporting = false;
                            new reportTQC().execute();
                            mBuilder.setContentText("Sending again...").setProgress(0,0,false);
                            notificationManager.notify(notificationId, mBuilder.build());
                        }else{
                            t.cancel();
                            isReporting = false;
                            Toast.makeText(getBaseContext(), "TQC fail", Toast.LENGTH_LONG);
                            mBuilder.setContentText("TQC failed").setProgress(0,0,false);
                            notificationManager.notify(notificationId, mBuilder.build());
                        }
                        Log.e("Error", trow+"");
                    }
                });
            }
            return null;
        }
    }

    /*
     * SEND-FULL IMAGE
     */
    public class sendFullImage extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server = prefs.getString("cf_server", "192.168.1.181");
            final String url = "http://"+ server + ":8080/WebServicesCellFusion/";
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            FullImage fullImage = new FullImage(tqcResult.getIdRec(), "PLW-QAS-S","EN", encoded);
            Call<List<SimpleResponse>> response = apiService.asignImage(fullImage);  //Return success
            response.enqueue(new Callback<List<SimpleResponse>>() {
                @Override
                public void onResponse(Call<List<SimpleResponse>> call, Response<List<SimpleResponse>> response) {
                   if(response.isSuccessful()) {
                       final Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               t.cancel();
                               mBuilder.setContentText("TQC Reported").setProgress(0,0,false);
                               notificationManager.notify(notificationId, mBuilder.build());
                               tqcReportedAlert = new AlertDialog.Builder(ReportActivity.this).create();
                               tqcReportedAlert.setTitle("TQC Reported: " + tqcResult.getRepNumber());
                               tqcReportedAlert.setMessage("The quality issue has been saved, a quality deviation report (TQC) has been saved and sent to the quality department and to all responsible with TQC reference number: " + tqcResult.getRepNumber());
                               tqcReportedAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                       new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int which) {
                                               tqcReportedAlert.dismiss();
                                               finish();
                                           }
                                       });
                               tqcReportedAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                   @Override
                                   public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                       if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                           switch (event.getKeyCode()) {
                                               case KeyEvent.KEYCODE_DPAD_DOWN:
                                                   mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                                                   mSpeechRecognizerManager.setOnResultListner(ReportActivity.this);
                                                   ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                                                   break;
                                               default:
                                                   break;
                                           }
                                       }
                                       return false;
                                   }
                               });
                               tqcReportedAlert.show();
                           }
                       }, 1500);

                       Button b = findViewById(R.id.button);
                       b.setText("Sent");
                   }
                }
                @Override
                public void onFailure(Call<List<SimpleResponse>> call, Throwable trgh) {
                    Log.e("Error", trgh+"");
                    t.cancel();
                    isReporting = false;
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
                        spinnerDefect = (TextView) findViewById(R.id.spinner_defect);
                        spinnerDefect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDefect.show();
                                defectSelection = true;
                            }
                        });
                        createDefectDialog();
                        if(auxDefects.size() > 0){
                            spinnerDefect.setText(auxDefects.get(0).getC_dscr());
                            defectSelected = auxDefects.get(0);
                        }
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
        ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m2);
        if(!comment) {
            if(defectSelection){
                if (commands.size() > 0 && commands.get(0).toLowerCase().contains("select")) {
                    alertDefect.dismiss();
                    defectSelection = false;
                    defectSelected = (Defects) defectList.getSelectedItem();
                    spinnerDefect.setText(defectSelected.getC_dscr());
                }
            }else if(prioSelection){
                if (commands.size() > 0 && commands.get(0).toLowerCase().contains("select")) {
                    alertPrio.dismiss();
                    prioSelection = false;
                    prioSelected = (priorities) prioList.getSelectedItem();
                    spinnerPrio.setText(prioSelected.getC_dscr());
                }
            }else if(issueSelection){
                if (commands.size() > 0 && commands.get(0).toLowerCase().contains("select")) {
                    alertIssues.dismiss();
                    issueSelection = false;
                    issueSelected = (Issue) issueList.getSelectedItem();
                    spinnerIssues.setText(issueSelected.getDscr());
                }
            }else{
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
                            break;
                        }
                    }
                    if (command.toLowerCase().contains("defect")) {
                        defectSelection = true;
                        alertDefect.show();
                        spinnerDefect.performClick();
                    }
                    if (command.toLowerCase().contains("decision") || command.toLowerCase().contains("decisión")  ||  command.toLowerCase().contains("desision")) {
                        issueSelection = true;
                        alertIssues.show();
                    }
                    if (command.toLowerCase().contains("priority")) {
                        spinnerPrio.performClick();
                        prioSelection = true;
                        alertPrio.show();
                    }
                    if (command.toLowerCase().contains("comments")) {
                        EditText editText = findViewById(R.id.comments);
                        editText.requestFocus();
                        mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                        mSpeechRecognizerManager.setOnResultListner(this);
                        ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                        comment = true;
                    }
                    if(command.toLowerCase().contains("send") || command.toLowerCase().contains("report")){
                        clickReport(null);

                        break;
                    }
                    if(command.toLowerCase().contains("confirm")){
                        if( tqcReportedAlert != null ){
                            tqcReportedAlert.dismiss();
//                            super.finish();
                            finish();
                        }
                        break;
                    }
                }
            }
        }else{
            EditText editText = findViewById(R.id.comments);
            editText.clearFocus();
            comment = false;
            String aux = editText.getText().toString();
            aux = commands.get(0).toLowerCase();
            editText.setText("");
            editText.setText(aux);
        }
    }

    public void addNotification() {
        mBuilder  = new NotificationCompat.Builder(getApplicationContext(), "DEFAULT_CHANNEL")
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("CellFusion")
                .setContentText("Sending TQC...")
                .setSmallIcon(R.drawable.qualityreport)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("DEFAULT_CHANNEL",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        mBuilder.setProgress(PROGRESS_MAX, notificationProgress, false);
        notificationManager.notify(notificationId, mBuilder.build() );
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
        @Override
            public void run() {
                mBuilder.setProgress(PROGRESS_MAX,notificationProgress,false);
                notificationManager.notify(notificationId, mBuilder.build());
                notificationProgress += 2;   //Called each time when 1000 milliseconds (1 second) (the period parameter)
            }},0,1000);
    }

    public void createDefectDialog(){
        defectAdapter = new TQCDefectAdapter(this, auxDefects);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        componentView = inflater.inflate(R.layout.tqc_alert_layout, null);
        defectList = (ListView)componentView.findViewById(R.id.tqc_element_list);
        defectList.setAdapter(defectAdapter);
        builder.setView(componentView);
        builder.setTitle("Defects available");
        builder.setIcon(R.drawable.defecticon);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDefect.dismiss();
                defectSelection = false;
            }
        });
        builder.setCancelable(false);
        alertDefect = builder.create();
        alertDefect.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(ReportActivity.this);
                            ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            alertDefect.dismiss();
                            defectSelection = false;
                            defectSelected = (Defects) defectList.getSelectedItem();
                            spinnerDefect.setText(defectSelected.getC_dscr());
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Priority dialog selection
     */
    public void createPriorityDialog(){
        prioAdapter = new TQCPrioAdapter(this, auxPrio);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        componentView = inflater.inflate(R.layout.tqc_alert_layout, null);
        prioList = (ListView)componentView.findViewById(R.id.tqc_element_list);
        prioList.setAdapter(prioAdapter);
        builder.setView(componentView);
        builder.setTitle("Priorities");
        builder.setIcon(R.drawable.prioicon);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertPrio.dismiss();
                prioSelection = false;
            }
        });
        builder.setCancelable(false);
        alertPrio = builder.create();
        alertPrio.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(ReportActivity.this);
                            ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            alertPrio.dismiss();
                            prioSelection = false;
                            prioSelected = (priorities) prioList.getSelectedItem();
                            spinnerPrio.setText(prioSelected.getC_dscr());
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Issues selection dialog
     */
    public void createIssuesDialog(){
        issueAdapter = new TQCIssueAdapter(this, auxIssues);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        componentView = inflater.inflate(R.layout.tqc_alert_layout, null);
        issueList = (ListView)componentView.findViewById(R.id.tqc_element_list);
        issueList.setAdapter(issueAdapter);
        builder.setView(componentView);
        builder.setTitle("Decision suggested");
        builder.setIcon(R.drawable.descsugested);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertIssues.dismiss();
                issueSelection = false;
            }
        });
        builder.setCancelable(false);
        alertIssues = builder.create();
        alertIssues.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            mSpeechRecognizerManager = new SpeechRecognizerManager(ReportActivity.this, true);
                            mSpeechRecognizerManager.setOnResultListner(ReportActivity.this);
                            ((ImageView)findViewById(R.id.tqc_mic)).setImageResource(R.drawable.m1);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            alertIssues.dismiss();
                            issueSelection = false;
                            issueSelected = (Issue) issueList.getSelectedItem();
                            if (issueSelected != null){
                                spinnerIssues.setText(issueSelected.getDscr());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSpeechRecognizerManager != null) {
            mSpeechRecognizerManager.destroy();
        }
        if( t != null){
            t.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
