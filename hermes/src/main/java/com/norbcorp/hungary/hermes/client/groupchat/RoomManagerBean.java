package com.norbcorp.hungary.hermes.client.groupchat;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

/**
 * 
 * @author nor
 *
 * RoomManagerBean class manages room instances of the logged user.
 *
 */
@Named
@SessionScoped
public class RoomManagerBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private XMPPConnectionManager xmppConnectionManager;
	@Inject
	private Client client;
	
	/**
	 * ResourceBundle is for showing error messages to the user.
	 */
	private ResourceBundle resourceBundle;
	
	/**
	 * Rooms which are created by the current user.
	 */
	private List<ChatRoom> listOfAvailableRooms;
	
	@PostConstruct
	public void init(){
		listOfAvailableRooms = new LinkedList<>();
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String messageBundleName = facesContext.getApplication().getMessageBundle();
		Locale locale = facesContext.getViewRoot().getLocale();
		resourceBundle = ResourceBundle.getBundle(messageBundleName, locale);
	}
	

	public void setListOfAvailableRooms(List<ChatRoom> listOfAvailableRooms) {
		this.listOfAvailableRooms = listOfAvailableRooms;
	}
	
	public List<ChatRoom> getListOfAvailableRooms() {
		return listOfAvailableRooms;
	}


	/**
	 * Create and add room to the list of available rooms.
	 * 
	 * @param roomName is the name of the room.
	 * @param subject is the subject of the room.
	 * @param listOfInvitees is the list of user names.
	 * @return <i>true</i> if the room was successfully created, <i>false</i> otherwise.
	 */
	boolean addRoom(String roomName, String subject, List<String> listOfInvitees){
		//Create a new room with unique JiD.
		ChatRoom room = ChatRoom.createRoom(roomName, client.getUserName(), client.getDomain(), subject, listOfInvitees);
		try {
			//It uses JiD to create a room
			xmppConnectionManager.createMultiUserChatRoom(room.getJid(), room.getSubject(), listOfInvitees);
			if(!listOfAvailableRooms.add(room)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.error.roomWasAlreadyAddedCannotBeCreatedTwice")));
				return false;
			}
		} catch (XMPPErrorException | NoResponseException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.error.roomCannotBeCreated")));
			return false;
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.info.roomWasSuccessfullyCreated")));
		return true;
	}
	
	void deleteRoom(String roomName, String reason){
	//	xmppConnectionManager.destroyMultiUserChat(roomName, reason);
	}
}
