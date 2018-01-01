package com.norbcorp.hungary.hermes.client.geolocation;

import java.io.Serializable;

public class EventItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id="";
	private String lng="";
	private String lat="";
	private String text="";
	private String locality="";

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
}
