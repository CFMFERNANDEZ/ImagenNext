package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.epson.moverio.btcontrol.DisplayControl;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;


/**
 * Created by CellFusion on 3/15/2018.
 */

public class SpeechRecognizerManager implements SensorEventListener {

    /*SENSOR CONSTANTS*/
    private final int TAPPED = 2;
    private SensorManager mSensorManager;
    private DisplayControl mDisplayControl;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "ok robot";
    private Context mContext;
    protected Intent mSpeechRecognizerIntent;
    protected android.speech.SpeechRecognizer mGoogleSpeechRecognizer;
    private OnResultListener mOnResultListener;


    public SpeechRecognizerManager(Context context) {
        this.mContext = context;
        initGoogleSpeechRecognizer();
//        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
//        if (mSensorManager != null) {
//            Sensor tap = mSensorManager.getDefaultSensor(Sensor.TYPE_HEADSET_TAP);
//            mSensorManager.registerListener(this, tap, SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }

    public SpeechRecognizerManager(Context context, boolean autoStart) {
        this(context);
        if(autoStart){
            mGoogleSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
//        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
//        if (mSensorManager != null) {
//            Sensor tap = mSensorManager.getDefaultSensor(Sensor.TYPE_HEADSET_TAP);
//            mSensorManager.registerListener(this, tap, SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }
    private void initGoogleSpeechRecognizer() {
        mGoogleSpeechRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(mContext);
        mGoogleSpeechRecognizer.setRecognitionListener(new GoogleRecognitionListener());
        mSpeechRecognizerIntent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra( RecognizerIntent. EXTRA_CONFIDENCE_SCORES, true);
    }

    public void destroy() {
        if (mGoogleSpeechRecognizer != null) {
            mGoogleSpeechRecognizer.cancel();
            mGoogleSpeechRecognizer.destroy();
        }
    }


    protected class GoogleRecognitionListener implements android.speech.RecognitionListener {

        private final String TAG = GoogleRecognitionListener.class.getSimpleName();

        @Override
        public void onBeginningOfSpeech() {
//            MediaPlayer mp = MediaPlayer.create(mContext, R.raw.googlenowtone);
//            mp.start();
            Log.d(TAG, "Google begining of speech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "Google END of speech");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onError(int error) {
            Log.e(TAG, "onError:" + error);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResultsheard:");
        }

        @Override
        public void onResults(Bundle results) {
            if ((results != null) && results.containsKey(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)) {
                ArrayList<String> heard = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                float[] scores = results.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);
                for (int i = 0; i < heard.size(); i++) {
                    Log.d(TAG, "onResultsheard:" + heard.get(i)+ " confidence:" + scores[i]);
                }
                //send list of words to activity
                if (mOnResultListener!=null){
                    mOnResultListener.OnResult(heard);
                }
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }

    public void setOnResultListner(OnResultListener onResultListener){
        mOnResultListener=onResultListener;
    }

    public interface OnResultListener
    {
        public void OnResult(ArrayList<String> commands);
    }

    //********SENSOR METHODS
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
