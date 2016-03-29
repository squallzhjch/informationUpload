package com.informationUpload.fragments;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.R.drawable;
import com.informationUpload.adapter.ChatAdapter;
import com.informationUpload.contentProviders.InformationManager;
import com.informationUpload.contentProviders.InformationManager.OnDBListener;
import com.informationUpload.contentProviders.Informations;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.DataBaseMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.entity.attachmentsMessage;
import com.informationUpload.entity.locationMessage;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.utils.Bimp;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.utils.InnerScrollView;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.utils.PoiRecordPopup.OnRecorListener;
import com.informationUpload.utils.WriteFileUtil;
import com.informationUpload.utils.ZipUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
/**
 * @author chentao
 * @version V1.0
 * @ClassName: InformationCollectionFragment
 * @Date 2016/03/24
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationCollectionFragment2 extends BaseFragment {
	/**
	 * info_fid
	 */
	private String mRowkey;
	/**
	 * 附件信息的基类
	 */
	private DataBaseMessage message;
	/**
	 * 语音信息数组
	 */
	private ArrayList<ChatMessage> chatList=new ArrayList<ChatMessage>();
	/**
	 * 图片信息数组
	 */
	private ArrayList<PictureMessage> picList=new ArrayList<PictureMessage>();
	/**
	 * 主view
	 */
	private View view;
	/**
	 * 返回按钮
	 */
	private RelativeLayout title_back;
	private TextView delete_photo;
	private TextView select_position_again;
	private InnerScrollView isv;
	private LinearLayout svll;
	private TextView mInformationCollectTitle;
	private TextView select_position;
	private ImageView hliv;
	private ListView voice_collection_lv;
	private HorizontalScrollView hscrollview;
	private LinearLayout hlinearlayout;
	private EditText additional_remarks_et;
	private Button savetolocal;
	private Button recordvoice;
	private Button report_at_once;
	private ArrayList<View> listview;
	private int width;
	private SimpleDateFormat df;
	private ChatAdapter adapter;
	private String main_remark;
	private String address;
	private int info_type;
	private String draw_txt;
	private Drawable draw_title;
	private ArrayList<String> list_pic_path=new ArrayList<String>();
	private ArrayList<String> list_chat_path=new ArrayList<String>();
	private int e;
	private int k;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ServiceEngin.DOWNLOAD_CHAT_SUCCESS:
				String chat_path=(String) msg.obj;
				list_chat_path.add(chat_path);
				if(list_chat_path.size()==chatList.size()){
					for(int i=0;i<list_chat_path.size();i++){
						chatList.get(i).setPath(list_chat_path.get(i));
					}
				}
				adapter = new ChatAdapter(getActivity(),null, chatList);
				resetListView();
				voice_collection_lv.setAdapter(adapter);
				break;
			case ServiceEngin.DOWNLOAD_PIC_SUCCESS:
				String pic_path=(String) msg.obj;
				list_pic_path.add(pic_path);
				if(list_pic_path.size()==picList.size()){
					for(int j=0;j<list_pic_path.size();j++){
						picList.get(j).setPath(pic_path);
					}
					queryinit();
				}
				break;
			}
		}
	};
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mRowkey = bundle.getString(SystemConfig.BUNDLE_DATA_ROWKEY);



		}
	}
	/**
	 * 请求后对控件进行初始化赋值
	 */
	private void queryinit(){
		additional_remarks_et.setText(main_remark);
		
		select_position.setText(address);
		for(int i=0;i<picList.size();i++){
			RelativeLayout.LayoutParams lp_rl = new RelativeLayout.LayoutParams(220,210);
			//			lp_rl.setMargins(10,0,10,0);

			RelativeLayout rl=new RelativeLayout(getActivity());

			rl.setLayoutParams(lp_rl);

			ImageView imageView = new ImageView(getActivity());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(210,210);
			lp.setMargins(5,0,5,0);

			imageView.setLayoutParams(lp);

			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			Log.i("info","geti lujing:"+picList.get(i).getPath());
			imageView.setImageURI(Uri.fromFile(new File(picList.get(i).getPath())));
			imageView.setTag(i);
			ImageView iv_delete = new ImageView(getActivity());

			RelativeLayout.LayoutParams lp_del = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);

			lp_del.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); 
			lp_del.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE); 
			lp_del.setMargins(2,2,5,2);

			iv_delete.setLayoutParams(lp_del);
			iv_delete.setScaleType(ImageView.ScaleType.FIT_XY);
			iv_delete.setBackgroundResource(R.drawable.delete_item);
		
			rl.addView(imageView);
			rl.addView(iv_delete);
			
			rl.setTag(listview.size());       
			hlinearlayout.addView(rl, 0);
			listview.add(rl);

			if (hscrollview.getWidth() >= width) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						hscrollview.scrollTo(hlinearlayout.getWidth() + 20, 0);

					}
				});
			}
		}
		
		switch(info_type){
		case Informations.Information.INFORMATION_TYPE_BUS:
			draw_txt="公交";
			draw_title=getActivity().getResources().getDrawable(R.drawable.bus_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_ESTABLISHMENT:
			draw_txt="设施";
			draw_title=getActivity().getResources().getDrawable(R.drawable.establishment_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_ROAD:
			draw_txt="道路";
			draw_title=getActivity().getResources().getDrawable(R.drawable.road_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_AROUND:
			draw_txt="周边其他";
			draw_title=getActivity().getResources().getDrawable(R.drawable.change_near_select_title);
			break;
		}
		mInformationCollectTitle.setText(draw_txt);
		mInformationCollectTitle.setCompoundDrawablesWithIntrinsicBounds (draw_title,
				null,null,null);
	}
	/**
	 * 初始化组件
	 */
	private void init() {
		listview = new ArrayList<View>();
		title_back = (RelativeLayout) view.findViewById(R.id.information_collect_back2);
		delete_photo=(TextView)view.findViewById(R.id.delete_photo2);
		select_position_again=(TextView)view.findViewById(R.id.select_position_again2);
		isv=    (InnerScrollView)     view.findViewById(R.id.isv2);
		svll=    (LinearLayout)     view.findViewById(R.id.svll2);

		mInformationCollectTitle= (TextView)view.findViewById(R.id.information_collect_title2);
		select_position = (TextView) view.findViewById(R.id.select_position2);
		hliv = (ImageView) view.findViewById(R.id.hliv2);
		voice_collection_lv = (ListView) view.findViewById(R.id.voice_collection_lv2);
		hscrollview = (HorizontalScrollView) view.findViewById(R.id.hscrollview2);
		hlinearlayout = (LinearLayout) view.findViewById(R.id.hlinearlayout2);

		additional_remarks_et = (EditText) view.findViewById(R.id.additional_remarks_et2);
		savetolocal = (Button) view.findViewById(R.id.savetolocal2);
		recordvoice = (Button) view.findViewById(R.id.recordvoice2);
		report_at_once = (Button) view.findViewById(R.id.report_at_once);
		
	}





	//重新计算高度
	public void resetListView() {

		int count = this.adapter.getCount();
		if(count!=0){
			View itemView = this.adapter.getView(0, null, null);
			if (itemView != null) {
				itemView.measure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

				int defHeight = count
						* (itemView.getMeasuredHeight() + voice_collection_lv
								.getDividerHeight());
				if(defHeight>180){
					LayoutParams lp =  voice_collection_lv.getLayoutParams();

					lp.height = defHeight;
					voice_collection_lv.setLayoutParams(lp);
				}
			}
		}
	}



	@Override
	public void onDetach() {
		super.onDetach();

	}
	@Override
	public void onDataChange(Bundle bundle) {

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view = inflater.inflate(R.layout.fragment2_information_collection, null);
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;     // 屏幕宽度（像素）
		df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//初始化
		init();
		
		if(!TextUtils.isEmpty(mRowkey)){
			query(mRowkey);
		}
		
		

		return view;
	}
	@Override
	public void onPause() {
		super.onPause();

	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
	}
	

	//10.情报数据提交任务信息查询接口
	public void query(String info_fid ){
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put("info_fid",info_fid);
		ServiceEngin.getInstance().Request(getActivity(), map,"inforqueryworkbyid", new EnginCallback(getActivity()){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				String result;
				result=arg0.result.toString();
				Log.e("请求成功", result);
				//对情报数据提交任务信息进行json解析
				parsesubmitJson(result);

			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}
		});

	}
	/**
	 * 对情报数据提交任务信息进行json解析
	 * @param result
	 */
	protected void parsesubmitJson(String result) {
		JSONObject jsonObj = JSON.parseObject(result);
		String errcode = jsonObj.getString("errcode");
		String errmsg = jsonObj.getString("errmsg");
		if(null!=errcode&&!"".equals(errcode)&&errcode.equals("0")){
			JSONArray data_ary = jsonObj.getJSONArray("data");

			for(int j=0;j<data_ary.size();j++){
				JSONObject data_obj=(JSONObject) data_ary.get(j);
				String user_id = data_obj.getString("user_id");
				String adminCode  = data_obj.getString("adminCode");
				String info_fid  =  data_obj.getString("info_fid");
				   info_type   =   data_obj.getIntValue("info_type");
				JSONObject locationObj = data_obj.getJSONObject("location");

				String lat = locationObj.getString("latitude");
				String lon=locationObj.getString("longitude");
				 address=data_obj.getString("address");
				 main_remark = data_obj.getString("remark");
				JSONArray attachment_list = data_obj.getJSONArray("attachments");
				for(int i=0;i<attachment_list.size();i++){
					JSONObject Obj = (JSONObject) attachment_list.get(i);
					String url = Obj.getString("url");
					int type = Obj.getIntValue("type");
					Log.i("info","type:"+type);
					String remark =  Obj.getString("remark");
					JSONObject location_obj = Obj.getJSONObject("location");
					double lat_att=location_obj.getDoubleValue("latitude");
					double lon_att=location_obj.getDoubleValue("longitude");
					String  time =  Obj.getString("time");
					if (type == 1) {
						message = new ChatMessage();
						chatList.add((ChatMessage) message);
					} else if (type == 0) {
						message = new PictureMessage();
						picList.add((PictureMessage) message);
					}

					if (!TextUtils.isEmpty(remark)){
						message.setRemark(remark);
					}

					if(!TextUtils.isEmpty(url)) {
						
						message.setPath(SystemConfig.MAIN_URL+url.substring(6));
					}

					message.setLat(lat_att);
					message.setLon(lon_att);
					message.setRowkey(info_fid);

				}
				for(int f=0;f<picList.size();f++){
					    e=f; 
						new Thread(new Runnable() {
							public void run() {
								try {
								ServiceEngin.getInstance().getFileStream(picList.get(e).getPath(), handler);
								
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
						
					
				}
				
				for(int h=0;h<chatList.size();h++){
					k=h;
					new Thread(new Runnable() {
						
						@Override
						public void run() {
						    try {
								ServiceEngin.getInstance().getFileStream(chatList.get(k).getPath(), handler);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}).start();
			   
				}
				
			
			}

		}else{
			Toast.makeText(getActivity(), errmsg, Toast.LENGTH_SHORT).show();
		}

	}

}
