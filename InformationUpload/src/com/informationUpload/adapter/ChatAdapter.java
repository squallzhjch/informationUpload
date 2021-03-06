package com.informationUpload.adapter;

import java.io.File;
import java.util.ArrayList;


import com.informationUpload.R;
import com.informationUpload.contentProviders.InformationManager;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.fragments.BaseFragment;
import com.informationUpload.fragments.InformationCollectionFragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import android.widget.TextView;
import android.widget.Toast;

public class ChatAdapter extends BaseAdapter {
	private ArrayList<ChatMessage> list;
	private LayoutInflater inflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private Context context;
	private InformationCollectionFragment fragm;
	private AnimationDrawable animation;
	private ViewHolder vh;
	private MediaPlayer md;
	public ChatAdapter(Context context,InformationCollectionFragment fragm,ArrayList<ChatMessage> list){
		this.fragm=fragm;
		this.context=context;
		if(list==null){
			list=new ArrayList<ChatMessage>();
		}else{
			this.list=list;
		}
		inflater=LayoutInflater.from(context);
	}

	public void setData(ArrayList<ChatMessage> list){
		this.list=list;
	}
	public void notifadataset(){
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	@Override
	public ChatMessage getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position ;
	}
	@Override
	public View getView(final int position, View Convertview, ViewGroup viewgroup) {
		vh=null;
		if(Convertview==null){ 
			vh=new ViewHolder();
			Convertview=inflater.inflate(R.layout.fm_card_chatting_item_msg_text_left, null);
			vh.iv_userhead=(ImageView)Convertview.findViewById(R.id.iv_userhead);
			vh.tv_chatcontent =(TextView) Convertview.findViewById(R.id.tv_chatcontent);
			vh.tv_time= (TextView)Convertview.findViewById(R.id.tv_time);
			vh.et_remark = (EditText) Convertview.findViewById(R.id.et_remark);
			Convertview.setTag(vh);
		}else{
			vh=(ViewHolder) Convertview.getTag();
		}
		try {
			md = new MediaPlayer();
			md.reset();
			md.setDataSource(getItem(position).getPath());
			md.prepare();
		} catch (Exception e) {
			e.printStackTrace();

		}
		long timeLong = md.getDuration();

		md.release();
		md=null;
		int longti= (int) timeLong;
        Log.i("chentao","time:"+longti);
		LayoutParams lp;

		lp = new LayoutParams(200+longti/1000*10,70);  

		vh.tv_chatcontent.setText("");
		vh.tv_chatcontent.setLayoutParams(lp);
		vh.tv_chatcontent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatto_voice_default, 0, 0, 0);
		vh.tv_time.setText(longti/1000+"''");
		vh.et_remark.setText(getItem(position).getRemark());  
		vh.iv_userhead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(fragm!=null){
					File file =new File(getItem(position).getPath());
					if(file.exists()){
						file.delete();
					}
//					list.remove(position);
					InformationManager.getInstance().deleteVideo(getItem(position).getRowkey(),getItem(position).getPath());
					fragm.removeChatList(position);
					
//					ChatAdapter.this.notifadataset();
					fragm.resetListView();
					Toast.makeText(context,"已经删除",Toast.LENGTH_LONG).show();
				}
	
			}
		});
		vh.tv_chatcontent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (getItem(position)!=null) {
					if(new File(getItem(position).getPath()).exists()){
						playMusic(getItem(position).getPath(), v) ;
					}else{
						Toast.makeText (context, "该文件已经被手动在文件夹中删除",Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		return Convertview;
	}
	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name,final View tv_chatcontent) {
		final TextView tv = (TextView)tv_chatcontent;
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);

			mMediaPlayer.prepare();
			mMediaPlayer.start();
			
			tv.setCompoundDrawablesWithIntrinsicBounds(R.anim.voice_img, 0, 0, 0);
			animation = (AnimationDrawable) tv.getCompoundDrawables()[0];

			animation.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					animation.stop();
					tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatto_voice_default, 0, 0, 0);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public class ViewHolder{
		public ImageView iv_userhead;
		public 	TextView tv_chatcontent;
		public TextView tv_time;
		public  EditText et_remark;
	}
}
