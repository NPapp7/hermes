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
public class CreateChatGroup implements Serializable{
	
	private static Logger logger = Logger.getLogger(CreateChatGroup.class.getName());
	
	@Inject
	private XMPPConnectionManager xmppConnectionManager;
	@Inject
	private Client client;
	@Inject
	private RoomManagerBean roomManagerBean;
	
	/**
	 * All available Contacts from which a user can select. 
	 */
	private List<Contact> contacts;
	
	/**
	 * Selected contacts invited to the chatRoom.
	 */
	private List<Contact> selectedContacts;
	
	/**
	 * Name of the chatRoom.
	 */
	private String roomName;
	
	/**
	 * Subject of a chatRoom.
	 */
	private String subject;
	
	/**
	 * It is necessary for creating a room.
	 * Reason why the room was created.
	 */
	private String reason;
	
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
	
	/**
	 * Delete the specified room from the RoomManager.
	 * Currently not supported by the server.
	 * 
	 * @param chatRoom which is to be deleted.
	 */
	public void deleteRoom(ChatRoom chatRoom){
		if(chatRoom.equals(chatRoom)){
			chatRoom=null;
		}
		roomManagerBean.deleteRoom(chatRoom);
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public void createRoomForParticipiants() throws XMPPErrorException, NoResponseException, NotConnectedException{
		List<String> selectedNames = selectedContacts.stream().map((contact)->contact.getContactName()).collect(Collectors.toList());
		roomManagerBean.addRoom(roomName, subject, selectedNames, this.reason);
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
	
	public String selectRoom(ChatRoom chat){
	//	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(chat.getJid()+" room was selected"));
		roomManagerBean.setSelectedRoom(chat);
		return "RoomSelected";
	}
	
}
