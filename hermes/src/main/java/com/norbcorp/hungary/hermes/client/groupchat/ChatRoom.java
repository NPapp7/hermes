package com.norbcorp.hungary.hermes.client.groupchat;

import java.io.Serializable;
import java.util.List;

public class ChatRoom implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Unique identifier of a room. It composed of the name of the user who requested the room.
	 * and room name. It is used by the server to identify the room.
	 */
	private String Jid;
	
	/**
	 * Name of the room in client side. It is different from Jid.
	 */
	private String roomName;
	
	/**
	 * List of invitees who were invited to the room.
	 */
	private List<String> listOfInvitees;
	
	/**
	 * Subject of the room.
	 */
	private String subject;
	
	public String getJid() {
		return Jid;
	}

	public void setJid(String jid) {
		Jid = jid;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public List<String> getListOfInvitees() {
		return listOfInvitees;
	}

	public void setListOfInvitees(List<String> listOfInvitees) {
		this.listOfInvitees = listOfInvitees;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String toString() {
		return "ChatRoom [Jid=" + Jid + ", roomName=" + roomName + ", listOfInvitees=" + listOfInvitees + ", subject="
				+ subject + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Jid == null) ? 0 : Jid.hashCode());
		result = prime * result + ((listOfInvitees == null) ? 0 : listOfInvitees.hashCode());
		result = prime * result + ((roomName == null) ? 0 : roomName.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatRoom other = (ChatRoom) obj;
		if (Jid == null) {
			if (other.Jid != null)
				return false;
		} else if (!Jid.equals(other.Jid))
			return false;
		if (listOfInvitees == null) {
			if (other.listOfInvitees != null)
				return false;
		} else if (!listOfInvitees.equals(other.listOfInvitees))
			return false;
		if (roomName == null) {
			if (other.roomName != null)
				return false;
		} else if (!roomName.equals(other.roomName))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	/**
	 * Create a room with JiD and roomName. Jid is roomName-userName@domainName.
	 * 
	 * @param domainName is the name of domain of the service. It is not stored. 
	 * @param roomName is the name of the user.
	 * @param userName is the name of User.
	 * @return a new Rooom instance.
	 */
	public static ChatRoom createRoom(String roomName, String userName, String domainName, String subject, List<String> listOfInvitees){
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setJid(roomName+userName);
		chatRoom.setRoomName(roomName);
		chatRoom.setListOfInvitees(listOfInvitees);
		chatRoom.setSubject(subject);
		return chatRoom;
	}
	
}
