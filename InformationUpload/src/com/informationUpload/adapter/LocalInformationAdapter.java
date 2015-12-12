package com.informationUpload.adapter;

import android.R.color;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informationUpload.R;
import com.informationUpload.contentproviders.InformationManager;
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
    private String type_str;//转成字符串的具体类型
	private String rowkey;

    public static final String[] KEY_MAPPING = new String[] {
            Informations.Information.ID, // Map to 0
            Informations.Information.TIME,//Map to 1
            Informations.Information.ROWKEY, //Map to 2
            Informations.Information.TYPE
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
   
		 CheckBox cb;
	     TextView type_tv;
		 ImageView iv_delete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() != null && getCursor().moveToPosition(position)) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_local_information, null);
               holder.cb= (CheckBox)convertView.findViewById(R.id.cb);
               holder.type_tv=(TextView)convertView.findViewById(R.id.type_tv);
               holder.iv_delete=(ImageView)convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
           
            rowkey = getCursor().getString(2);
            int type=getCursor().getInt(3);
            if(type==Informations.Information.INFORMATION_TYPE_BUS){
            	type_str="公交上报";
            }else if(type==Informations.Information.INFORMATION_TYPE_ESTABLISHMENT){
            	type_str="设施";
            }else if(type==Informations.Information.INFORMATION_TYPE_AROUND){
            	type_str="周边";
            }else if(type==Informations.Information.INFORMATION_TYPE_ROAD){
            	type_str="道路";
            }else if(type==Informations.Information.INFORMATION_TYPE_OTHER){
            	type_str="其他";
            }
            if(!TextUtils.isEmpty(rowkey)) {
            
                convertView.setTag(R.id.rowkey_id, rowkey);
            }
         
            holder.type_tv.setText(type_str);
            holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.i("chentao", "delete1");
					if(holder.cb.isChecked()==true){
						Log.i("chentao", "delete2:"+rowkey);
					InformationManager.getInstance().deleteInformation(rowkey);
					}
				}
			});
          
      
            
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
