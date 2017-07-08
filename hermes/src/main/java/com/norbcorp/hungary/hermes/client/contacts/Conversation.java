package com.norbcorp.hungary.hermes.client.contacts;

import java.io.Serializable;
import java.util.List;

public class Conversation implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<ContactMessage> messages;
	
	public Conversation() {
		super();
	}
	public Conversation(List<ContactMessage> messages) {
		super();
		this.messages = messages;
	}
	public List<ContactMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ContactMessage> messages) {
		this.messages = messages;
	}
}
