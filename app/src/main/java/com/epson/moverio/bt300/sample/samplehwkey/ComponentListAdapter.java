package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class ComponentListAdapter extends ArrayAdapter<Component>{

    private ArrayList<Component> components;
    private Context context;
    private TextView componentCode;
    private TextView componentDscr;
    private TextView componentQuantity;
    private ImageView componentImage;

    public ComponentListAdapter(Context context, ArrayList<Component> components){
        super(context, -1, components);
        this.context = context;
        this.components = components;
    }

    public int getCount(){
        return components.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.component_list, parent, false);
        componentCode = (TextView) rowView.findViewById(R.id.component_code);
        componentDscr = (TextView)rowView.findViewById(R.id.component_dscr);
        componentQuantity = (TextView)rowView.findViewById(R.id.component_quantity);

        componentCode.setText(components.get(i).getCode());
        componentDscr.setText(components.get(i).getDscr());
        componentQuantity.setText(components.get(i).getQuantity()+"");
        return rowView;
    }
}
