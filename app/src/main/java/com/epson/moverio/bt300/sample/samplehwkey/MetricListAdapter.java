package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class MetricListAdapter extends ArrayAdapter<Metric> {

    private List<Metric> metrics;
    private Context context;
    private TextView metricCode;
    private TextView spectedMin;
    private TextView spectedQuantity;
    private EditText inputQuantity;
    private ImageView componentImage;
    private ViewGroup parent;

   /* private float met = 0;
    private float metMin ;
    private float metMax;
    private View rowView = null;*/


    public MetricListAdapter(Context context, List<Metric> metrics){
        super(context, -1, metrics);
        this.context = context;
        this.metrics = metrics;
    }

    public int getCount(){return metrics != null ? metrics.size() : 0;}

    public long getItemId(int position){ return 0;}

    @Override
    public View getView(final int i, View view, ViewGroup parent){
        parent = parent;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.metrics_list, parent, false);
        metricCode = (TextView)rowView.findViewById(R.id.metric_code);
        spectedMin = (TextView)rowView.findViewById(R.id.metric_min);
        spectedQuantity = (TextView)rowView.findViewById(R.id.metric_spected);
        inputQuantity = (EditText)rowView.findViewById(R.id.metric_input);
        if(metrics != null && metrics.get(i).getMeasureInput() == null){
            metricCode.setText(metrics.get(i).getMeasure_dscr());
            spectedQuantity.setText(metrics.get(i).getMeasure_htarget()+"");
            spectedMin.setText(metrics.get(i).getMeasure_ltarget()+"");
            inputQuantity.requestFocus();
            inputQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                        Log.d("Accion", v.getText().toString()+"");
                        Log.d("EVENT", ""+ event);
                        metrics.get(i).setMeasureInput(v.getText().toString());
                        Float met = Float.parseFloat(v.getText().toString());
                        Float metMin = Float.parseFloat(spectedMin.getText().toString());
                        Float metMax = Float.parseFloat(spectedQuantity.getText().toString());
                        paintRows(met,metMin,metMax,rowView,i);

                    }
                    else{
                    }
                    inputQuantity.requestFocus();
                    return true;
                }
            });
        }

        if(metrics != null && metrics.get(i).getMeasureInput() != null){
            metricCode.setText(metrics.get(i).getMeasure_dscr());
            spectedQuantity.setText(metrics.get(i).getMeasure_htarget()+"");
            spectedMin.setText(metrics.get(i).getMeasure_ltarget()+"");
            inputQuantity.setText(metrics.get(i).getMeasureInput()+"");
            Float min = Float.parseFloat(metrics.get(i).getMeasure_ltarget());
            Float max = Float.parseFloat(metrics.get(i).getMeasure_htarget());
            Float auxMet = Float.parseFloat(metrics.get(i).getMeasureInput());
            paintRows(auxMet,min,max,rowView, i);

            inputQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                        Log.d("Accion", v.getText().toString()+"");
                        Log.d("EVENT", ""+ event);
                        Float met = Float.parseFloat(v.getText().toString());
                        Float metMin = Float.parseFloat(spectedMin.getText().toString());
                        Float metMax = Float.parseFloat(spectedQuantity.getText().toString());
                        paintRows(met,metMin,metMax,rowView,i);
                    }
                    else{
                    }
                    inputQuantity.requestFocus();
                    return true;
                }
            });

        }
        inputQuantity.requestFocus();
        return rowView;
    }

    public List<Metric> getListMetrics(){
        return metrics;
    }

    public void paintRows(Float met, Float min, Float max, View v, int i){

        if(met >= min && met <= max){
            v.setBackgroundResource(R.color.metricColorGreen);

        }else {
            v.setBackgroundResource(R.color.metricColorRed);
            metrics.get(i).setMeasureInput(null);
            inputQuantity.requestFocus();
           /* MetricListAdapter.super.notifyDataSetChanged();
            inputQuantity.requestFocus();
            metrics.get(i).setMeasureInput(null);
            if(metrics.get(i).getMeasureInput() == null){
                v.setBackgroundResource(R.color.metricColorRed);
            }
            Toast.makeText(getContext(), "The measured value must be between the measurements " + min + " and " + max, Toast.LENGTH_LONG).show();
            inputQuantity = v.findViewById(R.id.metric_input);
            inputQuantity.getText().clear();*/
        }
    }

    public void resetInput(){
        inputQuantity = (EditText)getView(0,null,parent).findViewById(R.id.metric_input);
        inputQuantity.requestFocus();
    }
}
