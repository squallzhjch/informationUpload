package com.informationUpload.fragments;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.R;
import com.informationUpload.activity.OffLineMapActivity;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.LoginHelper;
import com.informationUpload.tool.StringTool;
import com.informationUpload.utils.FileUtils;
import com.informationUpload.utils.RoundBitmapUtil;
import com.informationUpload.system.SystemConfig;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: CenterFragment
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class UserCenterFragment extends BaseFragment{
	/**
	 * 主view
	 */
	private View view;
	/**
	 * 上报记录
	 */
	private RelativeLayout mRecordLayout;
	/**
	 * 我的战绩
	 */
	private RelativeLayout mMyRecordLayout;
	/**
	 * 离线地图
	 */
	private RelativeLayout mOfflineMapLayout;
	/**
	 * 新闻中心
	 */
	private RelativeLayout mNewsCenterLayout;
	/**
	 * 清除缓存
	 */
	private RelativeLayout mClearCacheLayout;
	/**
	 * 意见反馈
	 */
	private RelativeLayout 	mFeedBackLayout;
	/**
	 * 关于
	 */
	private RelativeLayout mAboutLayout;
	/**
	 * 个人资料
	 */
	private ImageView mPersonData;
	/**
	 * 返回按钮
	 */
	private RelativeLayout mBack;
	/**
	 * 退出登录或者绑定手机号
	 */
	private Button mExitLogin;
	/**
	 * 我的账户
	 */
	private TextView mTvAccount;
	/**
	 * 下一步进入修改昵称界面
	 */
	private ImageView mIvHeadNext;
	/**
	 * 下一步进入修改昵称界面
	 */
	private RelativeLayout mRlHeadNext;
	/**
	 * 进入个人资料的relativelayout
	 */
	private RelativeLayout mRlHead;
	/**
	 * 昵称显示textview
	 */
	private TextView mTvName;
	private String userName;
	private String editionTime;
	private boolean islogin;
	private String path;
	private Bitmap initBitmap;

    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	registerOnContentUpdateListener(new AbstractOnContentUpdateListener() {
			
			@Override
			public String getKey() {
				// TODO Auto-generated method stub
				return SystemConfig.MODIFY_HEAD_PIC;
			}
			
			@Override
			public void onContentUpdated(List<Object[]> values) {
				editionTime= ConfigManager.getInstance().getEditionTime();
				islogin= ConfigManager.getInstance().isLogin();
				/**
				 * 将字符串数据转化为毫秒数
				 */
				Calendar c_init = Calendar.getInstance();
				try {
					c_init.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(editionTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(islogin){
						if(hasSDcard()){
							path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
						}else{
							path = SystemConfig.INNER_HEAD_IMG_PATH; 
						}
						String pathName_init = path+c_init.getTimeInMillis()+"_"+ConfigManager.getInstance().getUserId();
				
						File file_init=new File(pathName_init);
						if(file_init.exists()){
							final Bitmap bitmap_init = BitmapFactory.decodeFile(pathName_init);
							mPersonData.setImageBitmap(initBitmap);
							new Handler().post(new Runnable() {
								
								@Override
								public void run() {
									mPersonData.setImageBitmap(RoundBitmapUtil.toRoundBitmap(mPersonData,bitmap_init));
									
								}
							});
						
						}
				
		}
				
				
			}
			
			@Override
			public boolean isActive() {
				// TODO Auto-generated method stub
				return mIsActive;
			}
		});
    	registerOnContentUpdateListener(new AbstractOnContentUpdateListener() {
			
			@Override
			public void onContentUpdated(List<Object[]> values) {
				
				if (values != null) {
						userName = (String) values.get(0)[0];
						
						if(mTvName!=null&&userName!=null&&!userName.equals("")){
							mTvName.setText("我的名字:"+userName);
							
						}else{
							mTvName.setText("我的名字:昵称");
						}						
				}
			}
			@Override
			public boolean isActive() {
				return mIsActive;
			}
			@Override
			public String getKey() {
				return SystemConfig.MODIFY_USERNAME;
			}
		});
    }
	@Override
	public void onDataChange(Bundle bundle) {
		if(!ConfigManager.getInstance().isLogin()){
			mExitLogin.setText("登录/注册");
		}else{
			mExitLogin.setText("退出登录");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view = inflater.inflate(R.layout.fragment_user_center, null, true);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}

	/**
	 * 初始化
	 */
	private void init() {
		InputStream is = getResources().openRawResource(R.drawable.headshot);  

	    initBitmap = BitmapFactory.decodeStream(is);	
	
		mExitLogin     =(Button) view.findViewById(R.id.exit_login);
		mPersonData = (ImageView) view.findViewById(R.id.iv_head);
		 mRlHead= (RelativeLayout) view.findViewById(R.id.rl_head);
		mRecordLayout = (RelativeLayout) view.findViewById(R.id.report_record);
		mMyRecordLayout = (RelativeLayout) view.findViewById(R.id.my_record);

		mOfflineMapLayout = (RelativeLayout) view.findViewById(R.id.offline_map);

		mNewsCenterLayout = (RelativeLayout) view.findViewById(R.id.news_center);

		mClearCacheLayout = (RelativeLayout) view.findViewById(R.id.clear_cache);

		mFeedBackLayout = (RelativeLayout) view.findViewById(R.id.feedback);

		mAboutLayout = (RelativeLayout) view.findViewById(R.id.about);
		mBack=(RelativeLayout)view.findViewById(R.id.back);
		mTvAccount     = (TextView) view.findViewById(R.id.tv_account);
		mIvHeadNext   =  (ImageView) view.findViewById(R.id.iv_head_next);
		 mRlHeadNext   =   (RelativeLayout) view.findViewById(R.id.rl_head_next);
		  mTvName   =    (TextView)view.findViewById(R.id.tv_name);
		 String tel = ConfigManager.getInstance().getUserTel();
		 if(null!=tel&&!"".equals(tel)){
			 mTvAccount.setText("账号:"+tel);
		 }
		 String userName = ConfigManager.getInstance().getUserName();
			if(null!=userName&&!"".equals(userName)){
		       mTvName.setText("我的名字:"+userName);
			}else{
				mTvName.setText("我的名字:昵称");
			}
			
			editionTime= ConfigManager.getInstance().getEditionTime();
			islogin= ConfigManager.getInstance().isLogin();
			/**
			 * 将字符串数据转化为毫秒数
			 */
			Calendar c_init = Calendar.getInstance();
			try {
				c_init.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(editionTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(islogin){
					if(hasSDcard()){
						path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
					}else{
						path = SystemConfig.INNER_HEAD_IMG_PATH; 
					}
					String pathName_init = path+c_init.getTimeInMillis()+"_"+ConfigManager.getInstance().getUserId();
			
					File file_init=new File(pathName_init);
					if(file_init.exists()){
						final Bitmap bitmap_init = BitmapFactory.decodeFile(pathName_init);
						mPersonData.setImageBitmap(initBitmap);
						new Handler().post(new Runnable() {
							
							@Override
							public void run() {
								mPersonData.setImageBitmap(RoundBitmapUtil.toRoundBitmap(mPersonData,bitmap_init));
								
							}
						});
					
					}
			
	}
	}
	/**
	 * 注册监听器
	 */
	private void addListeners() {
		//下一步修改个人资料
		mRlHeadNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(PersonDataFragment.class, null));
				
			}
		});
		//下一步进入个人资料
		mIvHeadNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(PersonDataFragment.class, null));
				
			}
		});
		//绑定手机号或者退出登录
		mExitLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Bundle bundle = new Bundle();
				if(ConfigManager.getInstance().isLogin()) {
					bundle.putBoolean(SystemConfig.BUNDLE_DATA_LOGIN_OUT, true);
					LoginHelper.loginOut();
					ConfigManager.getInstance().setUserId(StringTool.createUserId());
				}

				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(LoginFragment.class, bundle));
			}
		});

		//返回
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();
			}
		});
		//进入个人资料界面按钮
		mRlHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(PersonDataFragment.class, null));
				
			}
		});
		//个人资料
		mPersonData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(PersonDataFragment.class, null));

			}
		});
		//上报记录
		mRecordLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(ReportRecordFragment.class, null));
			}
		});
		//我的战绩
		mMyRecordLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(MyRecordFragment
						.class, null));

			}
		});
		//离线地图
		mOfflineMapLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().startActivity(new Intent(getActivity(),OffLineMapActivity.class));

			}
		});
		//新闻中心
		mNewsCenterLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		//清除缓存
		mClearCacheLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					File file=new File(Environment.getExternalStorageDirectory() + "/MyPicture/");
					FileUtils.deleteFile(file);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
				}

				Toast.makeText(getActivity(),"清除缓存成功！",Toast.LENGTH_SHORT).show();
			}
		});
		//意见反馈
		mFeedBackLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		// 关于
		mAboutLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

	}
	public static boolean hasSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
