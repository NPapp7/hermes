package com.norbcorp.hungary.hermes.client.groupchat;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.norbcorp.hungary.hermes.client.Client;

public class GroupChatInvitationListener implements InvitationListener {

	private static Logger logger = Logger.getLogger(GroupChatInvitationListener.class.getName());
	
	private LinkedList<GroupChatInvitation> groupChatInvitations;
	
	public GroupChatInvitationListener(LinkedList<GroupChatInvitation> groupChatInvitations) {
		super();
		this.groupChatInvitations = groupChatInvitations;
	}

	@Override
	public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason,
			String password, Message message) {
		logger.info("Inviter: "+inviter+", reason: "+reason+", message: "+message);
		GroupChatInvitation groupChatInvitation = new GroupChatInvitation(inviter, message.getBody(),reason, room);
		if(!this.groupChatInvitations.contains(groupChatInvitation))
			this.groupChatInvitations.add(groupChatInvitation);
	}
}
