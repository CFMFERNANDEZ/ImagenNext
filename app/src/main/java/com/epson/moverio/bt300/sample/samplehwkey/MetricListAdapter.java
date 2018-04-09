package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView spectedQuantity;
    private TextView inputQuantity;
    private ImageView componentImage;

    public MetricListAdapter(Context context, List<Metric> metrics){
        super(context, -1, metrics);
        this.context = context;
        this.metrics = metrics;
    }

    public int getCount(){return metrics.size();}

    public long getItemId(int position){ return 0;}

    @Override
    public View getView(int i, View view, ViewGroup parent){
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.metrics_list, parent, false);
        metricCode = (TextView)rowView.findViewById(R.id.metric_code);
        spectedQuantity = (TextView)rowView.findViewById(R.id.metric_spected);
        inputQuantity = (EditText)rowView.findViewById(R.id.metric_input);
        metricCode.setText(metrics.get(i).getMeasure_code());
        spectedQuantity.setText(metrics.get(i).getMeasure_htarget()+"");
        return rowView;
    }
}
