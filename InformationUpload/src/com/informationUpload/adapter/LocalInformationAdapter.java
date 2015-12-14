package com.informationUpload.adapter;

import java.util.HashMap;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informationUpload.R;
import com.informationUpload.contentproviders.InformationManager;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.fragments.ReportRecordFragment;

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
	private View convertView;
	private HashMap<Integer, Boolean> map;
	private int position;

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
        map=new HashMap<Integer,Boolean>();
        mContext = context;
    }

    private LocalInformationAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    class ViewHolder {
   
		 CheckBox cb;
	     TextView type_tv;
	
    }

    @Override
    public View getView(int poi,  View View, ViewGroup parent) {
    	LocalInformationAdapter.this.convertView=View;
    	this.position=poi;
        if (getCursor() != null && getCursor().moveToPosition(position)) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_local_information, null);
               holder.cb= (CheckBox)convertView.findViewById(R.id.cb);
               holder.type_tv=(TextView)convertView.findViewById(R.id.type_tv);
               
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
            Log.i("chentao","getViewrowkey:"+rowkey);
            if(!TextUtils.isEmpty(rowkey)) {
                
                convertView.setTag(R.id.cb,rowkey);
               convertView.setTag(R.id.type_tv,holder.cb.isChecked());
//               map.put(position, holder.cb.isChecked());
//               ReportRecordFragment.set(position,holder.cb.isChecked());
            }
            if(ReportRecordFragment.get(position)==true){
            	holder.cb.setChecked(true);
            }else{
            	holder.cb.setChecked(false);
            }
            holder.type_tv.setText(type_str);
            holder.cb.setTag(position);
            holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//					if(arg1==true){
//						Log.i("chentao","onCheckedChanged1:"+arg1);
//						
//						convertView.setTag(R.id.type_tv ,arg1);
//					}else{
					
//						convertView.setTag(R.id.type_tv ,arg1);
					
						ReportRecordFragment.set((Integer) arg0.getTag(),arg1);
//					}
					
					
				}
			});
          
      
            
        }
        return convertView;
    }

   public boolean getpo(int po){
	return map.get(po);
	   
   }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
