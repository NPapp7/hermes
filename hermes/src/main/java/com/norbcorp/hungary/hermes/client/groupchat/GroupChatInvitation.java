package com.norbcorp.hungary.hermes.client.groupchat;

public class GroupChatInvitation {
	private String inviter;
	private String message;
	private String reason;
	
	public GroupChatInvitation(String inviter, String message, String reason) {
		super();
		this.inviter = inviter;
		this.message = message;
		this.reason = reason;
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
}
