package com.epson.moverio.bt300.sample.samplehwkey;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by CellFusion on 5/30/2018.
 */

public class CustomSwitch extends Switch {


    private OnCheckedChangeListener mListener;

    public CustomSwitch(Context context) {
        super(context);
    }

    public CustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        // Do not call supper method
        mListener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        if (mListener != null) {
            mListener.onCheckedChanged(this, checked);
        }
        Log.d("onListener","OnListener");
    }

    public void setCheckedProgrammatically(boolean checked) {
        // You can call super method, it doesn't have a listener... he he :)
        super.setChecked(checked);
    }
}