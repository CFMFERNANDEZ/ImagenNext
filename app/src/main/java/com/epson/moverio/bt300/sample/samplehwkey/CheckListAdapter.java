package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class CheckListAdapter extends ArrayAdapter<Metric> {

    private List<Metric> metrics;
    private Context context;
    private TextView metricCode;
    private TextView metricDscr;
    private ImageView componentImage;
    private Switch Sswitch;
    ViewGroup parentView;

    public CheckListAdapter(Context context, List<Metric> metrics){
        super(context, -1, metrics);
        this.context = context;
        this.metrics = metrics;
    }

    public int getCount(){return metrics != null ? metrics.size() : 0;}

    public long getItemId(int position){ return 0;}

    @Override
    public View getView(final int i, View view, ViewGroup parent){
        parentView = parent;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.checkmetrics_list, parent, false);
        metricCode = (TextView)rowView.findViewById(R.id.checkmetric_code);
        metricCode.setText(metrics.get(i).getMeasure_code());
        metricDscr = (TextView)rowView.findViewById(R.id.checkmetric_dscr);
        metricDscr.setText(metrics.get(i).getMeasure_dscr());
        Sswitch = (Switch) rowView.findViewById(R.id.check_switch);
        Sswitch.setChecked(metrics.get(i).getCheck());
        return rowView;
    }

    public List<Metric> getListMetrics(){
        return metrics;
    }

    public void setChecked(boolean state, int index){
        metrics.get(index).setCheck(state);
        View rowView = getView(index, null, parentView);
        Sswitch = (Switch)rowView.findViewById(R.id.check_switch);
        Log.d("setChecked", metrics.get(index).getCheck() + "");
        Sswitch.setChecked(true);
    }

    public View getView(final int i){
        return getView(i, null, parentView);
    }

}
