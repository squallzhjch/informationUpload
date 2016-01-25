package com.informationUpload.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import com.informationUpload.R;
import com.informationUpload.receiver.ConnectionChangeReceiver.ConChangeCallBack;
import com.umeng.analytics.MobclickAgent;




public class OffLineMapActivity extends Activity implements
MKOfflineMapListener, OnClickListener {

	private MKOfflineMap mOffline = null;
	private TextView cidView;
	private TextView stateView, allcityl_tv, hotcitylist_tv, finished_tv,
	localingTitle_tv;
	private EditText cityNameView;
	private Button localButton, clButton, user_back_btn;

	private ExpandableListView hotCityList;
	private ExpandableListView allCityList;
	private ExpandableListView downloadingmaplist;
	private ExpandableListView localMapListView;
	private Button update_all, download_all, pause_all;

	ArrayList<MKOLSearchRecord> records1;
	ArrayList<MKOLSearchRecord> records2;
	ArrayList<MKOLSearchRecord> records3;

	private boolean srearching = false;
	private int lastItem_unfinish;
	private int lastItem_finished;
	private int netStatus;
	/**
	 * 已下载的离线地图信息列表
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private ArrayList<MKOLUpdateElement> loadingMapList = null;
	private LoadMapListAdapter lAdapter = null;
	private LoadMapListAdapter loadingAdapter = null;
	private LocalProListAdapter localProListAdapter = null;
	private LocalProListAdapter hotCitysListAdapter = null;
	private BroadcastReceiver mReceiver;
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	private OffLineMapActivity mContext;
	private final String mPageName = "OffLineMapActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_offline);
		mContext=this;
		mOffline = new MKOfflineMap();
		mOffline.init(this);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					Log.d("mark", "网络状态已经改变");
					connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					info = connectivityManager.getActiveNetworkInfo();
					if (info != null && info.isAvailable()) {
						String name = info.getTypeName();
						Log.d("mark", "当前网络名称：" + name);
						netStatus = 1;
					} else {
						Log.d("mark", "没有可用网络");
						netStatus = -1;
						stopAll(null);
					}
				}
			}
		};

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);

		initView();

	}

	private void initView() {

		cidView = (TextView) findViewById(R.id.cityid);
		cityNameView = (EditText) findViewById(R.id.city);
		stateView = (TextView) findViewById(R.id.state);
		allcityl_tv = (TextView) findViewById(R.id.allcityl_tv);
		hotcitylist_tv = (TextView) findViewById(R.id.hotcitylist_tv);
		finished_tv = (TextView) findViewById(R.id.finished_tv);
		localingTitle_tv = (TextView) findViewById(R.id.localingTitle_tv);
		localButton = (Button) findViewById(R.id.localButton);
		clButton = (Button) findViewById(R.id.clButton);
		user_back_btn = (Button) findViewById(R.id.user_back_btn);
		localButton.setOnClickListener(this);
		clButton.setOnClickListener(this);
		user_back_btn.setOnClickListener(this);
		typeCheck(cityNameView);

		update_all = (Button) findViewById(R.id.update_all);
		download_all = (Button) findViewById(R.id.download_all);
		pause_all = (Button) findViewById(R.id.pause_all);

		update_all.setOnClickListener(this);
		download_all.setOnClickListener(this);
		pause_all.setOnClickListener(this);

		hotCityList = (ExpandableListView) findViewById(R.id.hotcitylist);
		hotCityList.setGroupIndicator(null);
		ArrayList<String> hotCities = new ArrayList<String>();
		// 获取热闹城市列表
		records1 = mOffline.getHotCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records1) {
				hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		hotCitysListAdapter = new LocalProListAdapter(OffLineMapActivity.this,
				records1);
		hotCityList.setAdapter(hotCitysListAdapter);

		hotCityList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int arg2, long arg3) {

				if (srearching == true) {
					// TODO Auto-generated method stub
					if (records3.get(arg2).childCities == null
							|| records3.get(arg2).childCities.size() == 0) {

						TextView tv = (TextView) arg1.findViewById(R.id.status);
						if (tv.getText().toString().equals("")) {

							if (netStatus == -1) {
								Toast.makeText(OffLineMapActivity.this,
										"网络异常，请检查网络", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(OffLineMapActivity.this,
										"已添加到下载任务", Toast.LENGTH_SHORT).show();
								addPre(records3.get(arg2));

							}

						} else {
							clickLocalMapListButton(null);
						}
					}
				} else {
					// TODO Auto-generated method stub
					if (records1.get(arg2).childCities == null
							|| records1.get(arg2).childCities.size() == 0) {

						TextView tv = (TextView) arg1.findViewById(R.id.status);
						if (tv.getText().toString().equals("")) {

							if (netStatus == -1) {
								Toast.makeText(OffLineMapActivity.this,
										"网络异常，请检查网络", Toast.LENGTH_SHORT)
										.show();
							} else {

								Toast.makeText(OffLineMapActivity.this,
										"已添加到下载任务", Toast.LENGTH_SHORT).show();
								addPre(records1.get(arg2));

							}

						} else {
							clickLocalMapListButton(null);
						}
					}
				}

				return false;
			}
		});

		allCityList = (ExpandableListView) findViewById(R.id.allcitylist);
		allCityList.setGroupIndicator(null);
		// 获取所有支持离线地图的城市
		ArrayList<String> allCities = new ArrayList<String>();
		records2 = mOffline.getOfflineCityList();
		if (records2 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}

		localProListAdapter = new LocalProListAdapter(OffLineMapActivity.this,
				records2);
		allCityList.setAdapter(localProListAdapter);

		allCityList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (records2.get(arg2).childCities == null
						|| records2.get(arg2).childCities.size() == 0) {

					TextView tv = (TextView) arg1.findViewById(R.id.status);
					if (tv.getText().toString().equals("")) {
						if (netStatus == -1) {
							Toast.makeText(OffLineMapActivity.this,
									"网络异常，请检查网络", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(OffLineMapActivity.this, "已添加到下载任务",
									Toast.LENGTH_SHORT).show();
							addPre(records2.get(arg2));

						}
					} else {
						clickLocalMapListButton(null);
					}

				}
				return false;
			}
		});

		// 获取已下过的离线地图信息
		localMapList = mOffline.getAllUpdateInfo();
		loadingMapList = new ArrayList<MKOLUpdateElement>();

		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		localMapListView = (ExpandableListView) findViewById(R.id.localmaplist);
		localMapListView.setGroupIndicator(null);
		lAdapter = new LoadMapListAdapter(OffLineMapActivity.this, false);
		localMapListView.setAdapter(lAdapter);

		downloadingmaplist = (ExpandableListView) findViewById(R.id.downloadingmaplist);
		downloadingmaplist.setGroupIndicator(null);
		loadingAdapter = new LoadMapListAdapter(OffLineMapActivity.this, true);
		downloadingmaplist.setAdapter(loadingAdapter);

		// 设置item点击的监听器
		localMapListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				return false;
			}
		});

		localMapListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {

				if (lastItem_finished >= 0) {

					if (lastItem_finished != groupPosition) {
						localMapListView.collapseGroup(lastItem_finished);
					}
				}
				lastItem_finished = groupPosition;
			}
		});

		downloadingmaplist
		.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {

				if (lastItem_unfinish >= 0) {
					if (lastItem_unfinish != groupPosition) {
						downloadingmaplist
						.collapseGroup(lastItem_unfinish);
					}
				}
				lastItem_unfinish = groupPosition;
			}
		});
		initLocalState();
		updateView();
		clickCityListButton(null);
	}

	public void initLocalState() {

		if (loadingMapList.size() == 0) {

			downloadingmaplist.setVisibility(View.GONE);
			localingTitle_tv.setVisibility(View.GONE);

			if (localMapList.size() == 0) {

				stateView.setVisibility(View.VISIBLE);
				stateView.setText("暂无下载，赶紧点击右侧\"城市列表\"进行下载！");
				localingTitle_tv.setVisibility(View.GONE);
				finished_tv.setVisibility(View.GONE);

			} else {
				stateView.setVisibility(View.GONE);
				finished_tv.setVisibility(View.GONE);
			}

		} else {
			stateView.setVisibility(View.GONE);
			downloadingmaplist.setVisibility(View.VISIBLE);
			localingTitle_tv.setVisibility(View.VISIBLE);

		}

		localingTitle_tv.setVisibility(View.GONE);
		finished_tv.setVisibility(View.GONE);

	}

	/**
	 * 切换至城市列表
	 * 
	 * @param view
	 */
	public void clickCityListButton(View view) {

		clButton.setBackgroundResource(R.drawable.main_leftbtn_off_xml);
		localButton.setBackgroundResource(R.drawable.main_leftbtn_xml);
		LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
		RelativeLayout lm = (RelativeLayout) findViewById(R.id.localmap_layout);
		lm.setVisibility(View.GONE);
		cl.setVisibility(View.VISIBLE);
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();
	}

	/**
	 * 切换至下载管理列表
	 * 
	 * @param view
	 */
	public void clickLocalMapListButton(View view) {

		clButton.setBackgroundResource(R.drawable.main_leftbtn_xml);
		localButton.setBackgroundResource(R.drawable.main_rightbtn_off_xml);
		LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
		RelativeLayout lm = (RelativeLayout) findViewById(R.id.localmap_layout);
		lm.setVisibility(View.VISIBLE);
		cl.setVisibility(View.GONE);
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();
	}

	/**
	 * 搜索离线需市
	 * 
	 * @param view
	 */
	public void search(View view) {

		allcityl_tv.setVisibility(View.INVISIBLE);
		allCityList.setVisibility(View.INVISIBLE);
		hotcitylist_tv.setVisibility(View.GONE);
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView
				.getText().toString());
		if (records == null || records.size() != 1)
			return;
		records1 = records;
		hotCityList
		.setAdapter(new MyAdapter(OffLineMapActivity.this, records1));

	}

	/**
	 * 城市检索
	 * 
	 */
	public boolean typeCheck(final EditText addrTxt) {

		addrTxt.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				checkType(addrTxt);
			}
		});

		return true;
	}

	/**
	 * 检索
	 * 
	 * @param cityNameView
	 */
	public void checkType(EditText cityNameView) {

		if (cityNameView.getText().toString().equals("")) {

			allcityl_tv.setVisibility(View.VISIBLE);
			allCityList.setVisibility(View.VISIBLE);
			hotcitylist_tv.setVisibility(View.VISIBLE);
			srearching = false;
			hotCitysListAdapter.setmDatas(records1);
			hotCityList.setAdapter(hotCitysListAdapter);
			hotCitysListAdapter.notifyDataSetChanged();

		} else {
			records3 = mOffline.searchCity(cityNameView.getText().toString());
			if (records3 == null || records3.size() != 1)
				return;

			allcityl_tv.setVisibility(View.GONE);
			allCityList.setVisibility(View.GONE);
			hotcitylist_tv.setVisibility(View.GONE);
			srearching = true;
			hotCitysListAdapter.setmDatas(records3);
			hotCityList.setAdapter(hotCitysListAdapter);
			hotCitysListAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void startAll(View view) {

		if (netStatus == -1) {
			Toast.makeText(this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
		} else {

			for (int i = 0; i < localMapList.size(); i++) {

				if (localMapList.get(i).status != localMapList.get(i).FINISHED) {
					localMapList.get(i).status = localMapList.get(i).WAITING;
				}

				int cityid = localMapList.get(i).cityID;
				mOffline.start(cityid);
			}
			download_all.setEnabled(false);
			pause_all.setEnabled(true);
			clickLocalMapListButton(null);
		}
	}

	/**
	 * 暂停下载
	 * 
	 * @param view
	 */
	public void stopAll(View view) {

		download_all.setEnabled(true);
		pause_all.setEnabled(false);
		if (netStatus == -1) {
			Toast.makeText(this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
		}
		for (int i = 0; i < localMapList.size(); i++) {

			int cityid = localMapList.get(i).cityID;
			if (localMapList.get(i).status != MKOLUpdateElement.FINISHED) {
				localMapList.get(i).status = MKOLUpdateElement.SUSPENDED;
				mOffline.pause(cityid);
			}
		}
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();
	}

	/**
	 * 暂停下载
	 * 
	 * @param view
	 */
	public void puase(int cityID) {

		pause_all.setEnabled(false);
		for (int i = 0; i < localMapList.size(); i++) {

			if (localMapList.get(i).cityID == cityID) {
				localMapList.get(i).status = MKOLUpdateElement.SUSPENDED;
			}
			if (localMapList.get(i).status == MKOLUpdateElement.DOWNLOADING) {
				pause_all.setEnabled(true);
			} else if (localMapList.get(i).status == MKOLUpdateElement.WAITING) {
				pause_all.setEnabled(true);
			}

		}
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();

		mOffline.pause(cityID);

	}

	/**
	 * 
	 * 本地添加离线地图
	 * 
	 * @param view
	 */
	public void addPre(MKOLSearchRecord msr) {

		stateView.setVisibility(View.GONE);
		MKOLUpdateElement me = new MKOLUpdateElement();
		me.cityName = msr.cityName;
		me.cityID = msr.cityID;
		me.ratio = 0;
		me.status = MKOLUpdateElement.WAITING;
		localMapList.add(me);
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();

		mOffline.start(msr.cityID);

	}

	/**
	 * 
	 * 开始本地离线记录
	 * 
	 * @param view
	 */
	public void startPreA(MKOLUpdateElement msr) {

		stateView.setVisibility(View.GONE);

		for (int i = 0; i < localMapList.size(); i++) {
			if (localMapList.get(i).cityID == msr.cityID) {
				localMapList.get(i).status = MKOLUpdateElement.WAITING;
				break;
			}
		}
		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();

		mOffline.start(msr.cityID);
	}

	/**
	 * 下载离线地图
	 * 
	 * @param cityid
	 */
	public void startDownload(int cityid) {

		mOffline.start(cityid);
		clickLocalMapListButton(null);
		// Toast.makeText(this, "��ʼ�������ߵ�ͼ. cityid: " + cityid,
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * 删除离线地图
	 * 
	 * @param view
	 */
	public void remove(View view) {

		// int cityid = Integer.parseInt(cidView.getText().toString());
		// mOffline.remove(cityid);
		// Toast.makeText(this, "ɾ�����ߵ�ͼ. cityid: " + cityid,
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * 从SD卡导入离线地图安装包
	 * 
	 * @param view
	 */
	public void importFromSDCard(View view) {
		int num = mOffline.importOfflineData();
		String msg = "";
		if (num == 0) {
			msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
		} else {
			msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);
			updateView();
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 更新状态显示
	 */
	public void updateView() {

		localMapList = mOffline.getAllUpdateInfo();
		loadingMapList = new ArrayList<MKOLUpdateElement>();

		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		initLocalState();

		lAdapter.notifyDataSetChanged();
		loadingAdapter.notifyDataSetChanged();
		hotCitysListAdapter.notifyDataSetChanged();
		localProListAdapter.notifyDataSetChanged();

		if (localMapList == null || localMapList.size() == 0) {

			update_all.setEnabled(false);
			download_all.setEnabled(false);
			pause_all.setEnabled(false);

		} else {

			update_all.setEnabled(false);
			download_all.setEnabled(false);
			pause_all.setEnabled(false);

			for (int i = 0; i < localMapList.size(); i++) {
				if (localMapList.get(i).status == MKOLUpdateElement.DOWNLOADING) {
					if (localMapList.get(i).ratio == 100) {
						pause_all.setEnabled(false);
					} else {
						pause_all.setEnabled(true);
					}

				} else if (localMapList.get(i).status == MKOLUpdateElement.WAITING) {
					pause_all.setEnabled(true);
				}
				if (localMapList.get(i).status == MKOLUpdateElement.SUSPENDED) {
					download_all.setEnabled(true);
				}
			}
		}

	}





	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		// mOffline.destroy();
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				// stateView.setText(String.format("%s : %d%%",
				// update.cityName,update.ratio));
				updateView();
			}
		}
		break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			MKOLUpdateElement e = mOffline.getUpdateInfo(state);
			Toast.makeText(this,"有新版本", Toast.LENGTH_SHORT).show();
			break;
		}

	}

	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter {

		private boolean isLoading;
		private ArrayList<MKOLUpdateElement> MapList = new ArrayList<MKOLUpdateElement>();

		public LocalMapAdapter(boolean isLoading) {

			this.isLoading = isLoading;
		}

		@Override
		public int getCount() {

			if (isLoading) {
				return loadingMapList.size();
			} else {
				return localMapList.size();
			}

		}

		@Override
		public Object getItem(int index) {

			if (isLoading) {
				return loadingMapList.get(index);
			} else {
				return localMapList.get(index);
			}
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(OffLineMapActivity.this,
					R.layout.offline_localmap_list, null);
			initViewItem(view, e);
			return view;
		}

		void initViewItem(View view, final MKOLUpdateElement e) {

			Button display = (Button) view.findViewById(R.id.display);
			Button remove = (Button) view.findViewById(R.id.remove);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);

			ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
			pb.setMax(100);

			if (e.ratio < 100) {
				if (pb.getVisibility() == View.GONE)
					pb.setVisibility(View.VISIBLE);
				pb.setProgress(e.ratio);
			} else {
				pb.setProgress(100);
				// 将 spinner 的可见性设置为不可见状态
				pb.setVisibility(View.GONE);
			}
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("可更新");
			} else {
				update.setText("最新");
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(OffLineMapActivity.this,
							BaseMapActivity.class);
					startActivity(intent);
				}
			});

		}

	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<MKOLSearchRecord> mDatas;

		public MyAdapter(Context context, List<MKOLSearchRecord> mDatas) {

			this.mInflater = LayoutInflater.from(context);
			this.mDatas = mDatas;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(
						R.layout.offline_map_list_layout, null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.size = (TextView) convertView.findViewById(R.id.size);
				holder.status = (TextView) convertView
						.findViewById(R.id.status);

				convertView.setTag(holder);
				convertView
				.setBackgroundResource(R.drawable.icon_list_item_default);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(mDatas.get(position).cityName);
			holder.size.setText(formatDataSize(mDatas.get(position).size));

			return convertView;
		}

	}

	public final class ViewHolder {

		public TextView title;
		public TextView size;
		public TextView status;
		public ImageView type_iv;

	}

	@Override
	public void onClick(View arg0) {

		int key = arg0.getId();

		switch (key) {

		case R.id.localButton:
			clickLocalMapListButton(null);
			break;

		case R.id.clButton:
			clickCityListButton(null);
			break;

		case R.id.user_back_btn:
			finish();
			break;

		case R.id.update_all:
			startAll(null);
			break;

		case R.id.download_all:
			startAll(null);

			break;

		case R.id.pause_all:
			stopAll(null);

			break;

		default:
			break;
		}
	}

	public class LoadMapListAdapter extends BaseExpandableListAdapter {

		private Context context;
		boolean isLoading;
		private LayoutInflater mInflater;

		public LoadMapListAdapter(Context context, boolean isLoading) {
			this.context = context;
			this.mInflater = LayoutInflater.from(context);
			this.isLoading = isLoading;
		}

		// 刷新
		public void refrush() {
			this.notifyDataSetChanged();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			if (isLoading) {
				return loadingMapList.get(groupPosition).cityName;
			} else {
				return localMapList.get(groupPosition).cityName;
			}
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final MKOLUpdateElement e = (MKOLUpdateElement) getGroup(groupPosition);
			convertView = mInflater.inflate(R.layout.offline_map_child_content,
					null);
			Button display = (Button) convertView
					.findViewById(R.id.show_offline);
			Button remove = (Button) convertView.findViewById(R.id.delete);
			final Button start_download = (Button) convertView
					.findViewById(R.id.start_download);
			convertView
			.setBackgroundResource(R.drawable.icon_list_item_default);

			if (e.ratio != 100) {
				start_download.setEnabled(true);
			} else {
				if (!e.update) {
					start_download.setText("开始下载");
					start_download.setEnabled(false);
				}
			}

			if (e.status == MKOLUpdateElement.DOWNLOADING) {
				start_download.setText("暂停");
			} else if (e.status == MKOLUpdateElement.WAITING) {
				start_download.setText("暂停");
			} else if (e.status == MKOLUpdateElement.SUSPENDED) {
				start_download.setText("开始下载");
			} else if (e.status == MKOLUpdateElement.UNDEFINED) {
				start_download.setText("开始下载");
			} else if (e.status == MKOLUpdateElement.eOLDSNetError) {
				start_download.setText("开始下载");
			} else if (e.status == MKOLUpdateElement.eOLDSWifiError) {
				start_download.setText("开始下载");
			}

			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (e.geoPt == null) {
						Toast.makeText(OffLineMapActivity.this, "暂无数据，请耐心等待！",
								Toast.LENGTH_SHORT).show();
					} else {

						Intent intent = new Intent();
						intent.putExtra("x", e.geoPt.longitude);
						intent.putExtra("y", e.geoPt.latitude);
						intent.putExtra("cityName", e.cityName);
						intent.setClass(OffLineMapActivity.this,
								MapControlActivity.class);
						startActivity(intent);
					}
				}
			});

			start_download.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (netStatus == -1) {

						Toast.makeText(OffLineMapActivity.this, "网络异常，请检查网络！",
								Toast.LENGTH_SHORT).show();

					} else {

						if (e.status == MKOLUpdateElement.DOWNLOADING) {

							puase(e.cityID);
							start_download.setText("开始下载");

						} else if (e.status == MKOLUpdateElement.WAITING) {

							puase(e.cityID);
							start_download.setText("开始下载");

						} else if (e.status == MKOLUpdateElement.SUSPENDED) {
							mOffline.start(e.cityID);
							startPreA(e);
							start_download.setText("暂停");

						} else if (e.status == MKOLUpdateElement.UNDEFINED) {
							startPreA(e);
							start_download.setText("暂停");
						}
					}
				}
			});
			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Object getGroup(int arg0) {

			if (isLoading) {
				return loadingMapList.get(arg0);
			} else {
				return localMapList.get(arg0);
			}
		}

		@Override
		public int getGroupCount() {

			if (isLoading) {
				return loadingMapList.size();
			} else {
				return localMapList.size();
			}
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(final int position, boolean isExpanded,
				View convertView, ViewGroup parent) {

			MKOLUpdateElement e = (MKOLUpdateElement) getGroup(position);
			convertView = View.inflate(OffLineMapActivity.this,
					R.layout.offline_localmap_list, null);
			convertView
			.setBackgroundResource(R.drawable.icon_list_item_default);
			initViewItem(convertView, e, isExpanded);
			return convertView;
		}

		void initViewItem(View view, final MKOLUpdateElement e,
				boolean isExpanded) {

			Button display = (Button) view.findViewById(R.id.display);
			Button remove = (Button) view.findViewById(R.id.remove);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ImageView suspend = (ImageView) view.findViewById(R.id.suspend);
			if (isExpanded) {
				suspend.setBackgroundResource(R.drawable.icon_poilist_down_arrow_select_up);
			} else {
				suspend.setBackgroundResource(R.drawable.offmap_expendbtn_xml);
			}

			ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
			pb.setMax(100);

			if (e.ratio < 100) {
				if (pb.getVisibility() == View.GONE)
					pb.setVisibility(View.VISIBLE);
				pb.setProgress(e.ratio);
			} else {
				pb.setProgress(100);
				// 将 spinner 的可见性设置为不可见状态
				pb.setVisibility(View.GONE);
			}

			ratio.setText(e.ratio + "%");

			if (e.status == MKOLUpdateElement.WAITING) {
				ratio.setText("等待下载");
			} else if (e.status == MKOLUpdateElement.SUSPENDED) {
				ratio.setText("已暂停" + e.ratio + "%");
			} else if (e.status == MKOLUpdateElement.UNDEFINED) {
				ratio.setText("UNDEFINED");
			} else if (e.status == MKOLUpdateElement.FINISHED) {
				ratio.setText("已下载");
			} else if (e.status == MKOLUpdateElement.DOWNLOADING) {
				if (e.ratio == 100) {
					ratio.setText("已下载");
				} else {
					ratio.setText("正在下载" + e.ratio + "%");
				}
			} else if (e.status == MKOLUpdateElement.eOLDSNetError) {
				ratio.setText("已暂停" + e.ratio + "%");
			} else if (e.status == MKOLUpdateElement.eOLDSWifiError) {
				ratio.setText("已暂停" + e.ratio + "%");
			}

			title.setText(e.cityName);
			if (e.update) {
				// update.setText("可更新");
				update.setText("最新");
			} else {
				update.setText("最新");
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}

			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.putExtra("cityName", e.cityName);
					intent.setClass(OffLineMapActivity.this,
							MapControlActivity.class);
					startActivity(intent);
				}
			});
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	// -------------------------------------------
	public class LocalProListAdapter extends BaseExpandableListAdapter {

		private LayoutInflater mInflater;
		private List<MKOLSearchRecord> mDatas;
		private int[] childsizes;

		public LocalProListAdapter(Context context,
				List<MKOLSearchRecord> mDatas) {

			this.mInflater = LayoutInflater.from(context);
			this.mDatas = mDatas;
			this.childsizes = new int[mDatas.size()];

		}

		public void setmDatas(List<MKOLSearchRecord> mDatas) {
			this.mDatas = mDatas;
			this.childsizes = new int[mDatas.size()];
		}

		// 刷新
		public void refrush() {
			this.notifyDataSetChanged();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return mDatas.get(groupPosition).childCities.get(childPosition);

		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final MKOLSearchRecord e = (MKOLSearchRecord) getGroup(groupPosition);
			final ArrayList<MKOLSearchRecord> mr = e.childCities;

			if (mr != null) {

				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = mInflater.inflate(
							R.layout.offline_map_list_layout, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.size = (TextView) convertView
							.findViewById(R.id.size);
					holder.status = (TextView) convertView
							.findViewById(R.id.status);
					holder.type_iv = (ImageView) convertView
							.findViewById(R.id.statusView);

					convertView.setTag(holder);
					convertView
					.setBackgroundResource(R.drawable.icon_list_item_default);

				} else {

					holder = (ViewHolder) convertView.getTag();
				}

				holder.title.setText(mr.get(childPosition).cityName);
				holder.size.setText(formatDataSize(mr.get(childPosition).size));
				holder.status.setText("");
				holder.type_iv.setEnabled(true);
				if (localMapList != null && localMapList.size() > 0) {

					for (int i = 0; i < localMapList.size(); i++) {
						if (localMapList.get(i).cityID == mr.get(childPosition).cityID) {
							if (localMapList.get(i).status == MKOLUpdateElement.DOWNLOADING) {
								if (localMapList.get(i).ratio == 100) {
									holder.status.setText("已下载");
									localMapList.get(i).status = MKOLUpdateElement.FINISHED;
								} else {
									holder.status.setText("下载中");
								}
								holder.type_iv.setEnabled(false);
								break;
							} else if (localMapList.get(i).status == MKOLUpdateElement.SUSPENDED) {
								holder.status.setText("暂停中");
								holder.type_iv.setEnabled(false);
								break;
							} else if (localMapList.get(i).status == MKOLUpdateElement.WAITING) {
								holder.status.setText("等待中");
								holder.type_iv.setEnabled(false);
								break;
							} else if (localMapList.get(i).status == MKOLUpdateElement.FINISHED) {
								holder.status.setText("已下载");
								holder.type_iv.setEnabled(false);
								break;
							} else if (localMapList.get(i).status == MKOLUpdateElement.eOLDSNetError) {
								holder.status.setText("已暂停");
								holder.type_iv.setEnabled(false);
								break;
							} else if (localMapList.get(i).status == MKOLUpdateElement.eOLDSWifiError) {
								holder.status.setText("wifi异常，已暂停");
								holder.type_iv.setEnabled(false);
								break;
							}

						}
					}
				}

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (netStatus == -1) {
							Toast.makeText(OffLineMapActivity.this,
									"网络异常，请检查网络", Toast.LENGTH_SHORT).show();
						} else {
							addPre(mr.get(childPosition));
							Toast.makeText(OffLineMapActivity.this, "已添加到下载任务",
									Toast.LENGTH_SHORT).show();

						}
					}
				});

			}
			return convertView;

		}

		@Override
		public int getChildrenCount(int arg0) {

			return (Integer) childsizes[arg0];
		}

		@Override
		public Object getGroup(int arg0) {

			return mDatas.get(arg0);

		}

		@Override
		public int getGroupCount() {

			return mDatas.size();

		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(final int position, boolean isExpanded,
				View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(
						R.layout.offline_map_pro_list_layout, null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.size = (TextView) convertView.findViewById(R.id.size);
				holder.status = (TextView) convertView
						.findViewById(R.id.status);
				holder.type_iv = (ImageView) convertView
						.findViewById(R.id.type_iv);

				convertView.setTag(holder);
				convertView
				.setBackgroundResource(R.drawable.icon_list_item_default);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(mDatas.get(position).cityName);
			holder.size.setText(formatDataSize(mDatas.get(position).size));

			final ArrayList<MKOLSearchRecord> mr = mDatas.get(position).childCities;
			holder.type_iv.setEnabled(true);
			if (mr != null && mr.size() > 0) {
				if (isExpanded) {
					holder.type_iv
					.setBackgroundResource(R.drawable.icon_poilist_down_arrow_select_up);
				} else {
					holder.type_iv
					.setBackgroundResource(R.drawable.offmap_expendbtn_xml);
				}

				childsizes[position] = mr.size();
				Log.e("mr", mr.size() + "yesyes-----------------");
			} else {

				Log.e("mr", "nono-----------------");
				holder.type_iv
				.setBackgroundResource(R.drawable.offmap_downloadbtn_xml);
				childsizes[position] = 0;

			}
			holder.status.setText("");
			if (localMapList != null && localMapList.size() > 0) {

				for (int i = 0; i < localMapList.size(); i++) {
					if (localMapList.get(i).cityID == mDatas.get(position).cityID) {
						if (localMapList.get(i).status == MKOLUpdateElement.DOWNLOADING) {
							if (localMapList.get(i).ratio == 100) {
								holder.status.setText("已下载");
								localMapList.get(i).status = MKOLUpdateElement.FINISHED;
							} else {
								holder.status.setText("下载中");
							}
							holder.type_iv.setEnabled(false);
							break;
						} else if (localMapList.get(i).status == MKOLUpdateElement.SUSPENDED) {
							holder.status.setText("暂停中");
							holder.type_iv.setEnabled(false);
							break;
						} else if (localMapList.get(i).status == MKOLUpdateElement.WAITING) {
							holder.status.setText("等待中");
							holder.type_iv.setEnabled(false);
							break;
						} else if (localMapList.get(i).status == MKOLUpdateElement.FINISHED) {
							holder.status.setText("已下载");
							holder.type_iv.setEnabled(false);
							break;
						} else if (localMapList.get(i).status == MKOLUpdateElement.eOLDSNetError) {
							holder.status.setText("已暂停");
							holder.type_iv.setEnabled(false);
							break;
						} else if (localMapList.get(i).status == MKOLUpdateElement.eOLDSWifiError) {
							holder.status.setText("wifi异常，已暂停");
							holder.type_iv.setEnabled(false);
							break;
						}
					}
				}

			}

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	class offMapConStatus implements ConChangeCallBack {
		@Override
		public void connectionStatus(int status) {
			netStatus = status;
			if (netStatus == -1) {
				// stopAll(null);
			}
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	public void onPause() {
		// int cityid = Integer.parseInt(cidView.getText().toString());
		// mOffline.pause(cityid);
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}

}