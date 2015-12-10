package com.informationUpload.adapter;

import java.util.ArrayList;


import com.informationUpload.R;
import com.informationUpload.entity.ChatMessage;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatAdapter extends BaseAdapter {
   
	
	private Context context;
	private ArrayList<ChatMessage> list;
	private LayoutInflater inflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();

	

	public ChatAdapter(Context context,ArrayList<ChatMessage> list){
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
		
		vh.tv_chatcontent.setText("");
		vh.tv_chatcontent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
		vh.tv_time.setText(getItem(position).getChattimelong()+"");
		
		vh.iv_userhead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			
				list.remove(position);
				
				
				ChatAdapter.this.notifadataset();
//				context.resetListView();
				
				
				Toast.makeText(context,"已经删除",Toast.LENGTH_LONG).show();
			}
		});
		vh.tv_chatcontent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				if (getItem(position)!=null) {
				  
					playMusic(getItem(position).getPath()) ;
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
