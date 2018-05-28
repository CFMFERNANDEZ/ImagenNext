package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by CellFusion on 4/16/2018.
 */

public class TrackingListAdapter extends ArrayAdapter<Component> {

    private List<Component> components;
    private Context context;
    private TextView componentCode;
    private TextView componenteDscr;
    private EditText trackInput;
    ViewGroup parentView;

    public TrackingListAdapter(Context context, List<Component> components){
        super(context, -1, components);
        this.context = context;
        this.components = components;
    }

    public int getCount(){ return components != null? components.size() : 0;};

    public long getItemId( int position ){ return 0; }

    @Override
    public View getView(final int i, View view, ViewGroup parent){
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        parentView = parent;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.tracking_list, parent, false);
        componentCode = (TextView)rowView.findViewById(R.id.tracking_comp_code);
        componentCode.setText(components.get(i).getCode());
        componenteDscr = (TextView)rowView.findViewById(R.id.tracking_comp_dscr);
        componenteDscr.setText(components.get(i).getDscr());
        if( components.get(i).gettrackingInput() != null &&  !components.get(i).gettrackingInput().equals("") ){
            trackInput = (EditText)rowView.findViewById(R.id.tracking_input);
            trackInput.setText( components.get(i).gettrackingInput() );
        }
        return rowView;
    }

    public boolean canContinue(){
        for ( int i = 0 ; i < components.size(); i++){
            if(components.get(i).gettrackingInput() == null){
                return false;
            }
        }
        return true;
    }

    public void setTracking(String tracking){
        Toast.makeText( context, tracking, Toast.LENGTH_LONG).show();
        for( int i = 0; i < components.size(); i++) {
            View rowView = getView(i, null, parentView);
            trackInput = (EditText) rowView.findViewById(R.id.tracking_input);
            if(components.get(i).gettrackingInput() == null || components.get(i).gettrackingInput().equals("")){
                trackInput.setText(tracking);
                components.get(i).settrackingInput(tracking);
                break;
            }
        }
    }

}
