package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
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

public class TQCIssueAdapter extends ArrayAdapter<Issue>{

    public List<Issue> issues;
    public Context context;
    ViewGroup parentView;

    public TQCIssueAdapter(Context context, List<Issue> issues){
        super( context, -1, issues);
        this.context = context;
        this.issues = issues;
    }

    public int getCount(){
        return issues != null? issues.size() : 0;
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
        componentCode.setText(issues.get(i).getDscr());
        if( i%2 == 0){
            rowView.setBackgroundResource(R.color.smart_alfa10);
        }else{
            rowView.setBackgroundResource(R.color.transparent);
        }
        return rowView;
    }

}
