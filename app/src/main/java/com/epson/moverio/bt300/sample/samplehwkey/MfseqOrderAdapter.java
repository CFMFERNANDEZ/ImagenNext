package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CellFusion on 3/20/2018.
 */

public class MfseqOrderAdapter extends ArrayAdapter<MfseqOrder> {
    private ArrayList<MfseqOrder> mfseqOrders;
    private Context context;
    private TextView asmDscr;
    private TextView asmCode;
    private TextView flowId;
    private TextView lotSerial;
    private ImageView status;

    public MfseqOrderAdapter(Context context, ArrayList<MfseqOrder> mfseqOrders ){
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
        View rowView = inflater.inflate(R.layout.mfseqorderlist, parent, false);

        asmDscr = (TextView) rowView.findViewById(R.id.mfseq_asmdscr);
        asmCode = (TextView) rowView.findViewById(R.id.mfseq_asmcode);
        flowId = (TextView) rowView.findViewById(R.id.mfseq_flowid);
        lotSerial = (TextView) rowView.findViewById(R.id.mfseq_serialn);
//        status = (ImageView) rowView.findViewById(R.id.mfseq_status);

        asmDscr.setText(mfseqOrders.get(i).getAsmDscr());
        asmCode.setText(mfseqOrders.get(i).getAsmCode());
        flowId.setText(mfseqOrders.get(i).getFlowId());
        lotSerial.setText(mfseqOrders.get(i).getLotSerial());

//        String s = mfseqOrders.get(i).getStatus();
//        if (s.contains("closed")) {
//            status.setImageResource(R.mipmap.closed);
//        } else if (s.contains("process")) {
//            status.setImageResource(R.mipmap.process);
//        } else {
//            status.setImageResource(R.mipmap.open);
//        }
        return rowView;
    }
}
