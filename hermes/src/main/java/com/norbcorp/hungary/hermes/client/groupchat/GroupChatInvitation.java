package com.norbcorp.hungary.hermes.client.groupchat;

import org.jivesoftware.smackx.muc.MultiUserChat;

public class GroupChatInvitation {
	private String inviter;
	private String message;
	private String reason;
	private MultiUserChat chatRoom;
	
	public GroupChatInvitation(String inviter, String message, String reason) {
		super();
		this.inviter = inviter;
		this.message = message;
		this.reason = reason;
	}

	public GroupChatInvitation(String inviter, String message, String reason, MultiUserChat chatRoom) {
		super();
		this.inviter = inviter;
		this.message = message;
		this.reason = reason;
		this.chatRoom = chatRoom;
	}
	
	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public MultiUserChat getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(MultiUserChat chatRoom) {
		this.chatRoom = chatRoom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inviter == null) ? 0 : inviter.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
		GroupChatInvitation other = (GroupChatInvitation) obj;
		if (inviter == null) {
			if (other.inviter != null)
				return false;
		} else if (!inviter.equals(other.inviter))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		return true;
	}
}
