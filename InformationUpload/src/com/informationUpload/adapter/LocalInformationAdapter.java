package com.informationUpload.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.informationUpload.R;
import com.informationUpload.contentproviders.Informations;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ReportRecordAdapter
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LocalInformationAdapter extends CursorAdapter {
    private Context mContext;

    public static final String[] KEY_MAPPING = new String[] {
            Informations.Information.ID, // Map to 0
            Informations.Information.TIME,//Map to 1
            Informations.Information.ROWKEY, //Map to 2
    };

    public static final String WHERE = Informations.Information.USER_ID + " = ? AND " + Informations.Information.STATUS + " = ?";
    public static final String ORDER_BY = Informations.Information.TIME;
    private LocalInformationAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public LocalInformationAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    private LocalInformationAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    class ViewHolder {
        TextView rowkey;
        TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() != null && getCursor().moveToPosition(position)) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_local_information, null);
                holder.rowkey = (TextView) convertView.findViewById(R.id.rowkey);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            String rowkey = getCursor().getString(2);
            if(!TextUtils.isEmpty(rowkey)) {
                holder.rowkey.setText(rowkey);
            }

            holder.time.setText(getCursor().getLong(1) + "");
        }
        return convertView;
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
