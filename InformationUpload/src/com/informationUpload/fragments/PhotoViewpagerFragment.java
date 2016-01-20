package com.informationUpload.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.informationUpload.R;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.system.SystemConfig;


import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.RelativeLayout;
/**
 * 点击图片，可放大，可删除界面
 * @author  chentao
 */
public class PhotoViewpagerFragment extends BaseFragment{
	private ArrayList<PictureMessage> listViews = new ArrayList<PictureMessage>();
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public int max;

	RelativeLayout photo_relativeLayout;
	/**
	 * 返回的主view
	 */
	private View view;
	/**
	 * 点击哪个图片
	 */
	private int position;
	
	  @Override
	    public void onAttach(Activity activity) {
		  super.onAttach(activity);
		  Bundle arg = getArguments();
		  if(arg != null){
			  position=arg.getInt(SystemConfig.BUNDLE_DATA_PICTURE_NUM);
			  
			  listViews=(ArrayList<PictureMessage>) arg.getSerializable(SystemConfig.BUNDLE_DATA_PICTURE_LIST);
		  }
	
	  }

	@Override
	public void onDataChange(Bundle bundle) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view =inflater.inflate(R.layout.photoviewpagerfragment,null);


		pager = (ViewPager) view.findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);


		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
	
		
		pager.setCurrentItem(position);
		return view;
	}


	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<PictureMessage> listViews;// content

		private int size;// 页数

		private ImageView iv;

		public MyPageAdapter(ArrayList<PictureMessage> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<PictureMessage> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			ImageView iv=new ImageView(getActivity());
			
			Uri uri = Uri.fromFile(new File(listViews.get(arg1 % size).getPath()));
			iv.setImageURI(uri);
//			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
			((ViewPager) arg0).removeView(iv);
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				 iv=new ImageView(getActivity());
				Uri uri = Uri.fromFile(new File(listViews.get(arg1 % size).getPath()));
				iv.setImageURI(uri);
//				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
				((ViewPager) arg0).addView(iv, 0);

			} catch (Exception e) {
			}
//			return listViews.get(arg1 % size);
			return iv;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
