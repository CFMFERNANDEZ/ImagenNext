package com.epson.moverio.bt300.sample.samplehwkey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WSMDSelectionActivity extends AppCompatActivity implements  ZXingScannerView.ResultHandler{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private static View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


     /*
     *
     * HERE ARE OUR CONSTANTS AND VALUES
     *
     * */
     private ZXingScannerView mScannerView;
     private static final int ALL_PERMISSION = 1;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};
     private boolean personnelLoaded = false;
     private boolean wsLoaded = false;
    //ArrayList<Personnel> personnels = new ArrayList();    //Works offline
    public static ArrayList<Personnel> PERSONNELS = new ArrayList();      //New List
    public static ArrayList<OrdersModel> orders = new ArrayList();
    public Personnel personSelected;
    public static String ID;
    public static Intent orderIntent;

    private static ImageView personnelPhoto;
    private static TextView personnelName;
    private static TextView personnelCode;
    private static TextView personnelEmail;
    private static TextView message;

    private APIService mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsmdselection);
        mVisible = true;
        mControlsView = findViewById(R.id.personnel_sublayout);
        mContentView = findViewById(R.id.personnel_sublayout);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (hasPermissions(this, PERMISSIONS)) {
                QrScanner();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSION);
            }
        }
        /*
        *   OUR CODE
        * */

        /*PERSONNELS.add( new Personnel("DE-4009","Jacqueline", "Bordeau"));
        PERSONNELS.add( new Personnel("CF-0931","Eliezer", "Beltran"));
        PERSONNELS.add( new Personnel("DE-6658","Max", "Kingsley"));*/

        //new loadJSON().execute();
        QrScanner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(personSelected != null){
            personnelPhoto = (ImageView)findViewById(R.id.personnel_image);
            personnelName = (TextView)findViewById(R.id.personnel_name);
            personnelCode = (TextView)findViewById(R.id.personnel_code);
            personnelEmail = (TextView)findViewById(R.id.personnel_email);
            message = (TextView)findViewById(R.id.wsmd_message);

            personnelPhoto.setImageResource(R.drawable.bordeau);
            personnelName.setText(personSelected.getC_lname()+" "+personSelected.getC_fname());
            personnelCode.setText(personSelected.getC_code());
            personnelEmail.setText(personSelected.getC_email());
            message.setText("Press Up button for scan your WS");
            wsLoaded = false;
        }
    }

    public class login extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            final String url = "http://192.168.1.9:8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            Call<List<Personnel>> person = apiService.getUsersByCode(ID);  //Return one record searching by CODE

            person.enqueue(new Callback<List<Personnel>>() {
                @Override
                public void onResponse(Call<List<Personnel>> call, Response<List<Personnel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        personSelected = response.body().get(0);

                        personnelPhoto = (ImageView)findViewById(R.id.personnel_image);
                        personnelName = (TextView)findViewById(R.id.personnel_name);
                        personnelCode = (TextView)findViewById(R.id.personnel_code);
                        personnelEmail = (TextView)findViewById(R.id.personnel_email);
                        message = (TextView)findViewById(R.id.wsmd_message);

                        //personnelPhoto.setImageResource(R.drawable.bordeau);
                        personnelName.setText(personSelected.getC_lname()+" "+personSelected.getC_fname());
                        personnelCode.setText(personSelected.getC_code());
                        personnelEmail.setText(personSelected.getC_email());
                        //personnelCode.setText(person.getUserCode());
                        message.setText("Press Right button for scan your WS");
                        personnelLoaded = true;

                    }else{
                        Toast.makeText(getApplicationContext(), "Personnel not valid", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<List<Personnel>> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
            return null;
        }
    }

    public class userImage extends  AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids){
            final String url = "http://192.168.1.166:8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            Call orders = apiService.getOrdersByWSMD(ID);  //Return one record searching by CODE

            orders.enqueue(new Callback<List<OrdersModel>>() {
                @Override
                public void onResponse(Call<List<OrdersModel>> call, Response<List<OrdersModel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {

                        Log.d("IMAGENRESPUESTA",response.toString());
                        //personnelPhoto = (ImageView)findViewById(R.id.personnel_image);


                    }else{
                        Toast.makeText(getApplicationContext(), "Workstation error", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OrdersModel>> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
            return null;
        }
    }

    public class wsmgSelection extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids){
            final String url = "http://192.168.1.9:8080/WebServicesCellFusion/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            Call<List<OrdersModel>> orders = apiService.getOrdersByWSMD(ID);  //Return one record searching by CODE

            orders.enqueue(new Callback<List<OrdersModel>>() {
                @Override
                public void onResponse(Call<List<OrdersModel>> call, Response<List<OrdersModel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        List<OrdersModel> auxList = new ArrayList();
                        int i = 0;
                        for( OrdersModel order : response.body()){
                            auxList.add(response.body().get(i));
                            i++;
                         }
                        orderIntent = new Intent(getBaseContext(),SelectionActivity.class);
                        orderIntent.putExtra("WMSD", ID);
                        orderIntent.putExtra("Orders",new OrdersModelList(auxList) );
                        startActivity(orderIntent);

                    }else{
                        Toast.makeText(getApplicationContext(), "Workstation error", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OrdersModel>> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
            return null;
        }
    }

    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(1200, TimeUnit.SECONDS)
            .connectTimeout(1200, TimeUnit.SECONDS)
            .build();

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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    QrScanner();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    public void handleResult(Result rawResult) {
        ID = rawResult.getText();
        mScannerView.stopCamera();
        setContentView(R.layout.activity_wsmdselection);
//        Intent orderIntent;
        if( !personnelLoaded){
            new login().execute();
            /*for(Personnel person : PERSONNELS){
                Log.d("ID PERSON", person.getC_code()+"");
                if( person.getC_code().equalsIgnoreCase(rawResult.getText())){
                    //int id = getResources().getIdentifier(person.getPhoto(), "drawable", getPackageName());
                    //Log.d("ID SOURCE", id +"");
                    personnelPhoto = (ImageView)mContentView.findViewById(R.id.personnel_image);
                    personnelName = (TextView)findViewById(R.id.personnel_name);
                    personnelCode = (TextView)findViewById(R.id.personnel_code);
                    message = (TextView)findViewById(R.id.wsmd_message);

                    //personnelPhoto.setImageResource(id);
                    personnelName.setText(person.getC_lname()+" "+person.getC_fname());
                    //personnelCode.setText(person.getUserCode());
                    message.setText("Press Right button for scan your WS");
                    personnelLoaded = true;
                }
            }
            if(!personnelLoaded){
                Toast.makeText(getApplicationContext(), "Personnel not valid", Toast.LENGTH_LONG).show();
            }*/
        }else if( !wsLoaded && personnelLoaded){
            new wsmgSelection().execute();
            Toast.makeText(getApplicationContext(), "Reading WS", Toast.LENGTH_LONG).show();
           /* switch ( rawResult.getText()){
                case "895221030116":
                    orderIntent = new Intent(this, SelectionActivity.class);
                    orderIntent.putExtra("WMSD", rawResult.getText());
                    startActivity(orderIntent);
                    break;
                case "889296895213":
                    orderIntent = new Intent(this, SelectionActivity.class);
                    orderIntent.putExtra("WMSD", rawResult.getText());
                    startActivity(orderIntent);
                    break;
                case "889296895176":
                    orderIntent = new Intent(this, SelectionActivity.class);
                    orderIntent.putExtra("WMSD", rawResult.getText());
                    startActivity(orderIntent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "WSMD not valid", Toast.LENGTH_LONG).show();
                    break;
            }*/
            wsLoaded = true;
        }
    }
}
