package com.norbcorp.hungary.hermes.client.groupchat;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;

import com.norbcorp.hungary.hermes.client.Client;

/**
 * Managed bean for user group communication.
 * It is for sending messages to a previously selected room.
 * 
 * @author nor
 *
 */
@Named
@ViewScoped
public class UserChatGroupBean implements Serializable{

	@Inject
	private Client client;
	
	@Inject
	private RoomManagerBean roomManagerBean;
	
	/**
	 * Selected room for the user.
	 */
	private ChatRoom selectedRoom;
	
	/**
	 * Message to the group.
	 */
	private String message;
	
	@PostConstruct
	public void init() {
		//Get the selected room for the user.
		this.selectedRoom =  this.roomManagerBean.getSelectedRoom();
	}
	
	public ChatRoom getSelectedRoom() {
		return selectedRoom;
	}

	public void setSelectedRoom(ChatRoom selectedRoom) {
		this.selectedRoom = selectedRoom;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Send specified message to a selected room.
	 */
	public void sendMessage(){
		roomManagerBean.sendMessage(message,selectedRoom);
	}
	
}
