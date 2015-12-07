package com.informationUpload.adapter;

import java.util.ArrayList;

import com.informationUpload.entity.ChatMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ChatAdapter extends BaseAdapter {
   
	
	private Context context;
	private ArrayList<ChatMessage> list;

	public ChatAdapter(Context context,ArrayList<ChatMessage> list){
		this.context=context;
		this.list=list;
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
	public View getView(int position, View Convertview, ViewGroup viewgroup) {
		
		return null;
	}

}
