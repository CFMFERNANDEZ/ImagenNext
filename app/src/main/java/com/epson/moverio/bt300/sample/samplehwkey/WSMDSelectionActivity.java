package com.epson.moverio.bt300.sample.samplehwkey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
//            hide();
        }

        ;
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
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
    private static String indMessage = "Press the right button to scan your workstation.";
    private APIService apiService;
    private Image personnelImage;

    private static String font_path = "font/EUEXCF.TTF";
    private static Typeface TF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsmdselection);
        mVisible = true;
        mControlsView = findViewById(R.id.personnel_sublayout);
        mContentView = findViewById(R.id.cflogo);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (hasPermissions(this, PERMISSIONS)) {
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSION);
            }
        }
        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.cf_logo);
        personnelPhoto = (ImageView)findViewById(R.id.personnel_image);
        personnelPhoto.setVisibility(View.GONE);
        personnelName = (TextView)findViewById(R.id.personnel_name);
        personnelCode= (TextView)findViewById(R.id.personnel_code);
        personnelEmail = (TextView)findViewById(R.id.personnel_email);
        message = (TextView)findViewById(R.id.wsmd_message);

        TF = Typeface.createFromAsset(getAssets(),font_path);
        personnelName.setTypeface(TF);
        personnelCode.setTypeface(TF);
        personnelEmail.setTypeface(TF);
        message.setTypeface(TF);

        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 380, 0, 0); // llp.setMargins(left, top, right, bottom);
        personnelName.setLayoutParams(llp);
        personnelName.setText("Welcome, press the right button to scan your personnel id.");
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//        QrScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(personSelected != null){
            TF = Typeface.createFromAsset(getAssets(),font_path);
            if( personnelImage != null){
                personnelPhoto = (ImageView)findViewById(R.id.personnel_image);
                personnelPhoto.setVisibility(View.VISIBLE);
                personnelPhoto.setImageBitmap(personnelImage.getImage());
            }
            personnelName = (TextView)findViewById(R.id.personnel_name);
            personnelCode = (TextView)findViewById(R.id.personnel_code);
            personnelEmail = (TextView)findViewById(R.id.personnel_email);
            message = (TextView)findViewById(R.id.wsmd_message);

            personnelName.setTypeface(TF);
            personnelCode.setTypeface(TF);
            personnelEmail.setTypeface(TF);
            message.setTypeface(TF);

            personnelName.setText(personSelected.getC_fname()+" "+personSelected.getC_lname());
            personnelCode.setText(personSelected.getC_code());
            personnelEmail.setText(personSelected.getC_email());
            message.setText(indMessage);
            wsLoaded = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_setting){
            Intent intent = new Intent(this, PreferenceActivityCustom.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public class login extends AsyncTask<Void,Void,Void> {
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
            apiService = retrofit.create(APIService.class);
            Call<List<Personnel>> person = apiService.getUsersByCode(ID);  //Return one record searching by CODE
            person.enqueue(new Callback<List<Personnel>>() {
                @Override
                public void onResponse(Call<List<Personnel>> call, Response<List<Personnel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        personSelected = response.body().get(0);
                        personnelName = (TextView)findViewById(R.id.personnel_name);
                        personnelCode = (TextView)findViewById(R.id.personnel_code);
                        personnelEmail = (TextView)findViewById(R.id.personnel_email);
                        message = (TextView)findViewById(R.id.wsmd_message);

                        TF = Typeface.createFromAsset(getAssets(),font_path);
                        personnelName.setTypeface(TF);
                        personnelCode.setTypeface(TF);
                        personnelEmail.setTypeface(TF);
                        message.setTypeface(TF);

                        personnelName.setText(personSelected.getC_fname()+" "+personSelected.getC_lname());
                        personnelCode.setText(personSelected.getC_code());
                        personnelEmail.setText(personSelected.getC_email());
                        message.setText(indMessage);
                        personnelLoaded = true;

                        Call<List<Image>> foto = apiService.getImage("personnel", personSelected.getC_id());
                        foto.enqueue(new Callback<List<Image>>() {
                            @Override
                            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                                personnelPhoto = (ImageView)findViewById(R.id.personnel_image);
                                if(response.isSuccessful()&& response.body().size() > 0) {
                                    personnelImage = response.body().get(0);
                                    if(personnelPhoto != null){
                                        personnelPhoto.setVisibility(View.VISIBLE);
                                        personnelPhoto.setImageBitmap(personnelImage.getImage());
                                    }
                                }else{
                                    if(personnelPhoto != null){
                                        personnelPhoto.setVisibility(View.VISIBLE);
                                        personnelPhoto.setImageResource(R.drawable.avatar);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Image>> call, Throwable t) {
                                Log.e("ERROR IMAGE", t.toString());
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "Personnel not valid", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<List<Personnel>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error with the server", Toast.LENGTH_LONG).show();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
            });
            return null;
        }
    }

    public class wsmgSelection extends AsyncTask<Void,Void,Void> {
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
            Call<List<OrdersModel>> orders = apiService.getOrdersByWSMD(ID);  //Return one record searching by CODE
            orders.enqueue(new Callback<List<OrdersModel>>() {
                @Override
                public void onResponse(Call<List<OrdersModel>> call, Response<List<OrdersModel>> response) {
                    if(response.isSuccessful()&& response.body().size() > 0) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Call<List<WSMDmodel>> wsmd = apiService.getWSMD(ID);
                        wsmd.enqueue(new Callback<List<WSMDmodel>>() {
                            @Override
                            public void onResponse(Call<List<WSMDmodel>> call, Response<List<WSMDmodel>> response) {
                                if(response.isSuccessful()&& response.body().size() > 0) {
                                    orderIntent.putExtra("WSMD", (WSMDmodel)response.body().get(0));
                                    startActivity(orderIntent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Workstation not valid", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<List<WSMDmodel>> call, Throwable t) {
                                Log.e("ERROR", t.toString());
                            }
                        });

                        List<OrdersModel> auxList = new ArrayList();
                        int i = 0;
                        for( OrdersModel order : response.body()){
                            auxList.add(response.body().get(i));
                            i++;
                         }
                        orderIntent = new Intent(getBaseContext(),SelectionActivity.class);
                        orderIntent.putExtra("Person", personSelected);
                        orderIntent.putExtra("Orders",new OrdersModelList(auxList) );

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
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        setContentView(mScannerView);
        try {
            mScannerView.startCamera(0);         // Start camera
        }catch (Exception e){
        }
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
        setContentView(R.layout.activity_wsmdselection);
        ID = rawResult.getText();
        mScannerView.stopCamera();
        if( !personnelLoaded){
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            new login().execute();
        }else if( !wsLoaded && personnelLoaded){
            if( personnelPhoto != null){
                personnelPhoto.setVisibility(View.GONE);
            }
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            new wsmgSelection().execute();
            wsLoaded = true;
        }
    }
}
