package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
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
    private TextView inputQuantity;
    private ImageView componentImage;

    public MetricListAdapter(Context context, List<Metric> metrics){
        super(context, -1, metrics);
        this.context = context;
        this.metrics = metrics;
    }

    public int getCount(){return metrics != null ? metrics.size() : 0;}

    public long getItemId(int position){ return 0;}

    @Override
    public View getView(int i, View view, ViewGroup parent){
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.metrics_list, parent, false);
        metricCode = (TextView)rowView.findViewById(R.id.metric_code);
        spectedMin = (TextView)rowView.findViewById(R.id.metric_min);
        spectedQuantity = (TextView)rowView.findViewById(R.id.metric_spected);
        inputQuantity = (EditText)rowView.findViewById(R.id.metric_input);
        if(metrics != null){
            metricCode.setText(metrics.get(i).getMeasure_code());
            spectedQuantity.setText(metrics.get(i).getMeasure_htarget()+"");
            spectedMin.setText(metrics.get(i).getMeasure_ltarget()+"");
            inputQuantity.requestFocus();

            inputQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean process = false;

                    if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                        Log.d("Accion", v.getText().toString()+"");
                        float met = Float.parseFloat(v.getText().toString());
                        float metMin = Float.parseFloat(spectedMin.getText().toString());
                        float metMax = Float.parseFloat(spectedQuantity.getText().toString());
                        if(met >= metMin && met <= metMax){
                            rowView.setBackgroundResource(R.color.metricColorGreen);



                        }else {
                            rowView.setBackgroundResource(R.color.metricColorRed);
                        }
                    }
                    else{

                    }


                    return false;
                }
            });
        }
        if( i%2 == 0){
            rowView.setBackgroundResource(R.color.smart_alfa10);
        }else{
            rowView.setBackgroundResource(R.color.transparent);
        }
        return rowView;
    }
}
