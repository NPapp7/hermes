package com.norbcorp.hungary.hermes.client.groupchat;

import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class GroupChatListener implements InvitationListener {

	private static Logger logger = Logger.getLogger(GroupChatListener.class.getName());
	
	@Override
	public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason,
			String password, Message message) {
		logger.info("Inviter: "+inviter+" reason "+reason+" message "+message);
	}
}
