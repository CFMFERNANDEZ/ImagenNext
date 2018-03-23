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
import java.util.ArrayList;


/**
 * Created by CellFusion on 3/15/2018.
 */

public class SpeechRecognizerManager implements SensorEventListener {

    /*SENSOR CONSTANTS*/
    private final int TAPPED = 2;
    private SensorManager mSensorManager;

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
        Log.d("Google VOICE", event.values[0]+"");
        if (event.sensor.getType() == Sensor.TYPE_HEADSET_TAP) {
            if (event.values[0] == TAPPED) {
                Log.d("Google VOICE", event.values[0]+"");
                mGoogleSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}