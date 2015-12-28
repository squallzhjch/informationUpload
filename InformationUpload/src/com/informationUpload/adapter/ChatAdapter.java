package com.informationUpload.adapter;

import java.io.File;
import java.util.ArrayList;


import com.informationUpload.R;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.fragments.InformationCollectionFragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
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
		ViewHolder vh=null;
		if(Convertview==null){ 
			vh=new ViewHolder();
			Convertview=inflater.inflate(R.layout.fm_card_chatting_item_msg_text_left, null);
			vh.iv_userhead=(ImageView)Convertview.findViewById(R.id.iv_userhead);
			vh.tv_chatcontent =(TextView) Convertview.findViewById(R.id.tv_chatcontent);
			vh.tv_time= (TextView)Convertview.findViewById(R.id.tv_time);
			Convertview.setTag(vh);
		}else{
		     vh=(ViewHolder) Convertview.getTag();
		}
		int longti= (int) (getItem(position).getChattimelong());
		LayoutParams lp;
		if(longti<2){
			 lp = new LayoutParams(longti*30,50);  
		}else{
			 lp = new LayoutParams(longti*16,50);  
		}
 	
		
		vh.tv_chatcontent.setText("");
		vh.tv_chatcontent.setLayoutParams(lp);
//		vh.tv_chatcontent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
		vh.tv_time.setText(getItem(position).getChattimelong()+"");
		
		vh.iv_userhead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				File file =new File(getItem(position).getPath());
				if(file.exists()){
					file.delete();
				}
				list.remove(position);
				
				
				ChatAdapter.this.notifadataset();
				
				fragm.resetListView();
				
				
				Toast.makeText(context,"已经删除",Toast.LENGTH_LONG).show();
			}
		});
		vh.tv_chatcontent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				if (getItem(position)!=null) {
				    if(new File(getItem(position).getPath()).exists()){
					playMusic(getItem(position).getPath()) ;
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
	private void playMusic(String name) {
		try {
			
			if (mMediaPlayer.isPlaying()) {
				
				mMediaPlayer.stop();
				
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		 
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					
					
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
	}

}
