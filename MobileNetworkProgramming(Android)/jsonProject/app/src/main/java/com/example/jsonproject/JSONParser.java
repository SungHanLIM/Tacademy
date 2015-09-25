package com.example.jsonproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
	private static final String TAG = "MainActivity";
	public static Melon getParser(String jsonString){
		Melon melon = null;
		JSONObject obj = null;
		JSONObject json = null;
		try{
			obj = new JSONObject(jsonString);
			json = obj.getJSONObject("melon");
			melon = new Melon();
			
			melon.setMenuId(json.getInt("menuId"));
			melon.setCount(json.getInt("count"));
			melon.setPage(json.getInt("page"));
			melon.setRankDay(json.getString("rankDay"));
			melon.setRankHour(json.getString("rankHour"));
			melon.setTotalPages(json.getInt("totalPages"));

			JSONObject songs = json.getJSONObject("songs");
			
			JSONArray songArr = songs.getJSONArray("song");
			int cnt = songArr.length();
			Log.v(TAG, "갯수  : " + cnt);
			Song song = null;
			JSONObject tempSong = null;
			ArrayList<Artist> artists ; 
			
			int aCnt = 0;
			JSONArray artistArr = null;
			JSONObject art;
			JSONObject tempArtist = null;
			Artist artist = null;
			for(int i = 0; i < cnt ; i++){
				tempSong = songArr.getJSONObject(i);
				song = new Song();
				song.setAdult(tempSong.getBoolean("isAdult"));
				song.setAlbumId(tempSong.getInt("albumId"));
//				song.setAlbumName(tempSong.getString("albumName"));
				artists = new ArrayList<Artist>();
				art = tempSong.getJSONObject("artists");
				artistArr = art.getJSONArray("artist");
				aCnt = artistArr.length();
				for(int j = 0; j < aCnt ; j++){
					tempArtist = artistArr.getJSONObject(j);
					artist = new Artist();
					artist.setArtistId(tempArtist.getInt("artistId"));
					artist.setArtistName(tempArtist.getString("artistName"));
					artists.add(artist);
				}
				song.setArtists(artists);
				
				song.setCurrentRank(tempSong.getInt("currentRank"));
				song.setFree(tempSong.getBoolean("isFree"));
				song.setHitSong(tempSong.getBoolean("isHitSong"));
				song.setIssueDate(tempSong.getString("issueDate"));
				song.setPastRank(tempSong.getInt("pastRank"));
				song.setPlayTime(tempSong.getInt("playTime"));
				song.setSongId(tempSong.getInt("songId"));
				song.setSongName(tempSong.getString("songName"));
				song.setTitleSong(tempSong.getBoolean("isTitleSong"));
				
				melon.getSongs().add(song);
			}
			Log.v(TAG, "JSON Parser success cnt : "  + cnt);
		}catch(Exception e){
			Log.v(TAG, "errro : " + e);
		}
		return melon;
	}
}
