package com.example.jsonproject;

import java.io.Serializable;

public class Artist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4715738656438161198L;

	
	private int artistId;
	private String artistName;
	public int getArtistId() {
		return artistId;
	}
	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}


}
