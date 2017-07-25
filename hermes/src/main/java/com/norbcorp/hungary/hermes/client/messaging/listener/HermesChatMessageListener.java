package com.norbcorp.hungary.hermes.client.messaging.listener;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import com.norbcorp.hungary.hermes.client.contacts.ContactMessage;
import com.norbcorp.hungary.hermes.client.contacts.Conversation;

/**
 * Listener class for getting messages from contactsm
 * 
 * @author NPapp7
 */
public class HermesChatMessageListener implements ChatMessageListener {

	private static Logger logger = Logger.getLogger(HermesChatMessageListener.class.getName());
	
	private HashMap<String, Conversation> chatHistory;
	
	public HermesChatMessageListener(HashMap<String,Conversation> chatHistory){
		this.chatHistory=chatHistory;
	}
	
	@Override
	public void processMessage(Chat chat, Message message) {
		String from = message.getFrom();
		String body = message.getBody();

		if (body != null && from != null) {

			// to get rid of the client name
			from = from.split("/")[0];

			if (!chatHistory.containsKey(from)) {
				List<ContactMessage> messages = new LinkedList<ContactMessage>();
				messages.add(new ContactMessage(from, body, Instant.now()));
				chatHistory.put(from, new Conversation(messages));
			} else {
				chatHistory.get(from).getMessages().add(new ContactMessage(from, body, Instant.now()));
			}
		}
		//logger.info(chatHistory.size()+"");
	}

}
