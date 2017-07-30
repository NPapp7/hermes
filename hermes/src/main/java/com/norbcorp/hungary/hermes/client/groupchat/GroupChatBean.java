package com.norbcorp.hungary.hermes.client.groupchat;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;
import com.norbcorp.hungary.hermes.client.contacts.Contact;

@Named
@ViewScoped
public class GroupChatBean implements Serializable {

	private static Logger logger = Logger.getLogger(GroupChatBean.class.getName());
	
	@Inject
	private XMPPConnectionManager xmppConnectionManager;
	@Inject
	private Client client;
	@Inject
	private RoomManagerBean roomManagerBean;
	
	private List<Contact> contacts;
	private List<Contact> selectedContacts;
	private String roomName;
	private String subject;
	private ChatRoom selectedRoom;
	
	/**
	 * Message to the selected room.
	 */
	private String message;
	
	@PostConstruct
	public void init() {
		try {
			contacts=xmppConnectionManager.getContacts();
			selectedContacts=new LinkedList<Contact>();
		} catch (NotLoggedInException | NotConnectedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}

	public void setSelectedContacts(List<Contact> selectedContacts) {
		this.selectedContacts = selectedContacts;
	}
	
	public void executeSelectedContacts(){
		this.selectedContacts.stream().forEach(System.out::println);
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public ChatRoom getSelectedRoom() {
		return selectedRoom;
	}

	public void setSelectedRoom(ChatRoom selectedRoom) {
		this.selectedRoom = selectedRoom;
	}

	public void createRoomForParticipiants() throws XMPPErrorException, NoResponseException, NotConnectedException{
		List<String> selectedNames = selectedContacts.stream().map((contact)->contact.getContactName()).collect(Collectors.toList());
		roomManagerBean.addRoom(roomName, subject, selectedNames);
	}
	
	public void selectRoom(ChatRoom chat){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(chat.getJid()+" room was selected"));
		this.selectedRoom=chat;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void sendMessage(){
		roomManagerBean.sendMessage(message,selectedRoom);
	}
	
	public void deleteRoom(ChatRoom chatRoom){
		if(chatRoom.equals(selectedRoom)){
			this.selectedRoom=null;
		}
		roomManagerBean.deleteRoom(chatRoom);
	}
}
