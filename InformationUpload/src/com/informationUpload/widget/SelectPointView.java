package com.informationUpload.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.informationUpload.R;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SelectPointView
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SelectPointView extends FrameLayout {

    private Context mContext;
    private TextView mAddress;
    private Button mSubmit;
    public SelectPointView(Context context) {
        this(context, null);
    }

    public SelectPointView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectPointView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.widget_select_point, null);

        mAddress = (TextView)view.findViewById(R.id.point_address);
        mSubmit = (Button)view.findViewById(R.id.ok_btn);
        addView(view);
    }

    public void setAddressText(CharSequence address){
        mAddress.setText(address);
    }

    public void setOnSubmitListener(OnClickListener onClickListener){
        if(onClickListener != null){
            mSubmit.setOnClickListener(onClickListener);
        }
    }
}
