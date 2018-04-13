package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CellFusion on 3/20/2018.
 */

public class MfseqOrderAdapter extends ArrayAdapter<OrdersModel> {
    private ArrayList<OrdersModel> mfseqOrders;
    private Context context;
    private TextView asmDscr;
    private TextView asmCode;
    private TextView quantity;
    private TextView lotSerial;
    private TextView eventPos;
    private ImageView status;

    public MfseqOrderAdapter(Context context, ArrayList<OrdersModel> mfseqOrders ){
        super(context, -1, mfseqOrders);
        this.mfseqOrders = mfseqOrders;
        this.context = context;
//        notifyDataSetChanged();
    }

    public int getCount(){
        return mfseqOrders.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.mfseqorder_list, parent, false);

        asmDscr = (TextView) rowView.findViewById(R.id.mfseq_asmdscr);
        asmCode = (TextView) rowView.findViewById(R.id.mfseq_asmcode);
        quantity = (TextView) rowView.findViewById(R.id.mfseq_quantity);
        lotSerial = (TextView) rowView.findViewById(R.id.mfseq_serialn);
        status = (ImageView) rowView.findViewById(R.id.mfseq_status);
        eventPos = (TextView) rowView.findViewById(R.id.event_pos);


        asmDscr.setText(mfseqOrders.get(i).getAsm_dscr());
        asmCode.setText(mfseqOrders.get(i).getAsm_code());
        quantity.setText(mfseqOrders.get(i).getQty());
        lotSerial.setText(mfseqOrders.get(i).getLotno());
        eventPos.setText("Event: "+mfseqOrders.get(i).getEvent_pos());
        if( i%2 == 0){
            rowView.setBackgroundResource(R.color.smart_alfa10);
        }else{
            rowView.setBackgroundResource(R.color.transparent);
        }
        if( mfseqOrders.get(i).getStatus_dscr().contains("process") ){
            status.setImageResource(R.drawable.process);
        }else if( mfseqOrders.get(i).getStatus_dscr().contains("Closed") ){
            status.setImageResource(R.drawable.closed);
        }else if( mfseqOrders.get(i).getStatus_dscr().contains("Open") ){
            status.setImageResource(R.drawable.open);
        }
        return rowView;
    }
}
