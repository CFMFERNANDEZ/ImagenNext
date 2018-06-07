package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CellFusion on 6/6/2018.
 */

public class TQCPrioAdapter extends ArrayAdapter<priorities>{

    public List<priorities> priorities;
    public Context context;
    ViewGroup parentView;

    public TQCPrioAdapter(Context context, List<priorities> priorities){
        super( context, -1, priorities);
        this.context = context;
        this.priorities = priorities;
    }

    public int getCount(){
        return priorities != null? priorities.size() : 0;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        parentView = parent;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.spinner_list_item, parent, false);
        TextView componentCode = (TextView) rowView.findViewById(R.id.spinerItem);
        componentCode.setText(priorities.get(i).getC_dscr());
        if( i%2 == 0){
            rowView.setBackgroundResource(R.color.smart_alfa10);
        }else{
            rowView.setBackgroundResource(R.color.transparent);
        }
        return rowView;
    }

}
