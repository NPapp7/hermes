package com.norbcorp.hungary.hermes.client.groupchat;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;
import com.norbcorp.hungary.hermes.client.contacts.ContactMessage;

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
	
	private static final Logger logger = Logger.getLogger(RoomManagerBean.class.getName());
	
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
	
	
	/**
	 * Chat room which was selected by the user.
	 */
	private ChatRoom selectedRoom;
	
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
	
	public ChatRoom getSelectedRoom() {
		return selectedRoom;
	}


	public void setSelectedRoom(ChatRoom selectedRoom) {
		this.selectedRoom = selectedRoom;
	}


	/**
	 * Create and add room to the list of available rooms.
	 * Also create listeners of invitations and messages.
	 * 
	 * @param roomName is the name of the room.
	 * @param subject is the subject of the room.
	 * @param listOfInvitees is the list of user names.
	 * @return <i>true</i> if the room was successfully created, <i>false</i> otherwise.
	 */
	boolean addRoom(String roomName, String subject, List<String> listOfInvitees,String reason){
		//Create a new room with unique JiD.
		final ChatRoom room = ChatRoom.createRoom(roomName, client.getUserName(), client.getDomain(), subject, listOfInvitees);
		try {
			//It uses JiD to create a room
			room.setMultiUserChat(xmppConnectionManager.createMultiUserChatRoom(room.getJid(), room.getSubject(), listOfInvitees, reason));
			//Adding messages to the room.
			room.getMultiUserChat().addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Message message) {
					room.getConversation().getMessages().add(new ContactMessage(message.getFrom().split("/")[1], message.getBody(),Instant.now()));
				}
			});
			//Fired when an invitation was declined
			room.getMultiUserChat().addInvitationRejectionListener(new InvitationRejectionListener() {
				@Override
				public void invitationDeclined(String invitee, String reason) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,resourceBundle.getString("groupChat.warn.invitationDeclined")+invitee,""));	
				}
			});
			if(!listOfAvailableRooms.add(room)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.error.roomWasAlreadyAddedCannotBeCreatedTwice")));
				return false;
			}
		} catch (XMPPErrorException | NoResponseException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.error.roomCannotBeCreated")));
			return false;
		} catch( java.lang.IllegalStateException ise){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ise.getMessage(),""));
			return false;
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(resourceBundle.getString("groupChat.info.roomWasSuccessfullyCreated")));
		return true;
	}
	
	void deleteRoom(ChatRoom chatRoom){
		try {
			chatRoom.getMultiUserChat().destroy("", "");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,resourceBundle.getString("groupChat.info.theRoom")+" "+chatRoom.getJid()+" "+resourceBundle.getString("groupChat.info.wasDeleted"),""));
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void sendMessage(String message, ChatRoom chatRoom){
		xmppConnectionManager.sendMessageToChatRoom(message, chatRoom);
	}
	
	/**
	 * JoinRoom method is for adding rooms to available rooms from invitations.
	 * 
	 * @param groupChatInvitation for which we want to create a room.
	 */
	public void joinRoom(GroupChatInvitation groupChatInvitation){
		try {
			groupChatInvitation.getChatRoom().join(client.getUserName());
			final ChatRoom chatRoom = ChatRoom.createRoom(groupChatInvitation.getChatRoom().getRoom().split("@")[0], groupChatInvitation.getInviter(), "", groupChatInvitation.getChatRoom().getSubject(), groupChatInvitation.getChatRoom().getOccupants());
			
			//Create xmpp multi user chat room for this particular chat room.
			chatRoom.setMultiUserChat(xmppConnectionManager.getMultiUserChatRoom(groupChatInvitation.getChatRoom().getRoom().split("@")[0]));
			chatRoom.getMultiUserChat().addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Message message) {
					chatRoom.getConversation().getMessages().add(new ContactMessage(message.getFrom().split("/")[1], message.getBody(),Instant.now()));
				}
			});
			listOfAvailableRooms.add(chatRoom);
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
