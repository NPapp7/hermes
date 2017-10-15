package com.norbcorp.hungary.hermes.client.messaging.listener;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.contacts.ContactMessage;
import com.norbcorp.hungary.hermes.client.contacts.Conversation;

/**
 * Listener class for getting messages from contactsm
 * 
 * @author NPapp7
 */
public class HermesChatMessageListener implements ChatMessageListener {

	private static Logger logger = Logger.getLogger(HermesChatMessageListener.class.getName());
	
	@Inject
	private Client client;
	
	private HashMap<String, Conversation> chatHistory;
	
	/**
	 * 
	 * @param chatHistory
	 */
	public HermesChatMessageListener(HashMap<String,Conversation> chatHistory){
		this.chatHistory=chatHistory;
	}
	
	@Override
	public void processMessage(Chat chat, Message message) {
		String from = message.getFrom();
		String body = message.getBody();

		//Message is not created when body and from are not null. 
		//Logged user cannot be the same who sent the message
		if (body != null && from != null) {

			// to get rid of the client domain name
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
