package com.norbcorp.hungary.hermes.client.contacts;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class ContactMessage implements Serializable, Comparable<ContactMessage>{
	
	private static final long serialVersionUID = 1L;
	private String userName;
	private String message;
	private Instant time;
	
	public ContactMessage() {
		super();
	}
	public ContactMessage(String userName, String message, Instant time) {
		super();
		this.userName = userName;
		this.message = message;
		this.time = time;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTime() {
		return Date.from(time);
	}
	public void setTime(Instant time) {
		this.time = time;
	}
	
	@Override
	public int compareTo(ContactMessage o) {
		return this.getTime().compareTo(o.getTime());
	}
}
