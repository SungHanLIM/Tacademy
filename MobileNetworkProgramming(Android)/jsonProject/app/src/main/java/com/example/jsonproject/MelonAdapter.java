package com.example.jsonproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MelonAdapter extends BaseAdapter {

	Context context;
	int layout;
	Melon data;
	public MelonAdapter(Context context, int layout, Melon data){
		this.context = context;
		this.layout = layout;
		this.data = data;
	}
	public void setData(Melon data){
		this.data = data;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.getSongs().get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View cView, ViewGroup parent) {
		if(cView == null){
			cView = View.inflate(context, layout, null);
		}
		Song song = data.getSongs().get(position);
		
		TextView tv = (TextView)cView.findViewById(R.id.rankId);
		tv.setText(song.getCurrentRank() + "");
		
		tv = (TextView)cView.findViewById(R.id.pastRank);
		tv.setText(song.getPastRank() + "");
		
		tv = (TextView)cView.findViewById(R.id.title);
		tv.setText(song.getSongName() + "");
		
		tv = (TextView)cView.findViewById(R.id.time);
		int time = song.getPlayTime();
		String tTime =  time / 60 + " : " + time % 60; 
		tv.setText(tTime);
		tv = (TextView)cView.findViewById(R.id.album);
		tv.setText(song.getAlbumName() );
		String artist = "";
		
		for(Artist aritsta : song.artists){
			artist += aritsta.getArtistName() + " ";
		}
		
		tv = (TextView)cView.findViewById(R.id.songName);
		tv.setText( artist );
		return cView;
	}

}
