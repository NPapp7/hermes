package com.norbcorp.hungary.hermes.client.messaging;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;
import com.norbcorp.hungary.hermes.client.contacts.Contact;

/**
 * Bean for sending and receiving messages.
 * 
 * @author nor
 *
 */
@Named
@ViewScoped
public class MessagingBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(MessagingBean.class.getName()); 

	private String messages;
	private String currentMessage;
	private String userJIDAndDomain;
	
	/**
	 * List of available contacts who are ready for talk.
	 */
	private List<Contact> listOfAvailableContacts;

	/**
	 * Contact which was selected for messaging.
	 */
	private Contact selectedContact;
	
	@Inject
	private XMPPConnectionManager xmppManager;
	@Inject
	private Client client;
	
	@PostConstruct
	public void init(){
		try{
			listOfAvailableContacts = new LinkedList<Contact>();
			Roster roster = xmppManager.getRoster();
			roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
			roster.reload();
			int id = 0;
			for (RosterEntry entry : xmppManager.getRosters()) {
				//The first entry is always the logged user.
				if(!(client.getUserName().equalsIgnoreCase(entry.getUser().split("@")[0]))){
					Contact c = new Contact();
					Presence presence = roster.getPresence(entry.getUser());
					logger.info("Found available user: "+entry.getUser());
					id += 1;
					c.setId(id);
					c.setContactName(entry.getUser().split("@")[0]);
					c.setPresenceType(presence.getType().name());
					c.setPresenceStatus(presence.isAvailable());
					c.setPresenceTextStatus(presence.getStatus());
		
					if (listOfAvailableContacts.contains(c)) {
						listOfAvailableContacts.get(listOfAvailableContacts.indexOf(c)).setPresenceStatus(c.isPresenceStatus());
					} else
						listOfAvailableContacts.add(c);
				}
			}
		}catch(NotLoggedInException|NotConnectedException|InterruptedException exc){
			logger.warning("Exception occured:"+exc);
		}
	}
	
	/**
	 * List of available contacts. 
	 * 
	 * @return list of contacts who are available and can get messages.
	 * @throws InterruptedException 
	 * @throws NotConnectedException 
	 * @throws NotLoggedInException 
	 */
	public List<Contact> getListOfAvailableContacts() throws NotLoggedInException, NotConnectedException, InterruptedException {
		return listOfAvailableContacts;
	}

	public void setListOfAvailableContacts(List<Contact> listOfAvailableContacts) {
		this.listOfAvailableContacts = listOfAvailableContacts;
	}

	public Contact getSelectedContact() {
		return selectedContact;
	}

	public void setSelectedContact(Contact selectedContact) {
		this.selectedContact = selectedContact;
	}
	
	public void selectForCommunication(){
		logger.info(this.selectedContact+"");
	}
	
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public String getCurrentMessage() {
		return currentMessage;
	}
	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}
	
	public String getUserJIDAndDomain() {
		return userJIDAndDomain;
	}
	public void setUserJIDAndDomain(String userJIDAndDomain) {
		this.userJIDAndDomain = userJIDAndDomain;
	}
	
	/**
	 * It sends the currentMessage value
	 * 
	 * @param userName user name without domain name
	 * @throws Exception
	 */
	public void sendMessage(String userName) throws Exception{
		logger.info("Send user message to "+userName+", "+client.getDomain());
		userJIDAndDomain=userName+"@"+client.getDomain();
		if(userJIDAndDomain==null || userJIDAndDomain.equals("") || currentMessage==null || currentMessage.equals(""))
		{
			return;
		}
		else{
			xmppManager.sendMessage(currentMessage, userJIDAndDomain);
			currentMessage="";
		}
	}
}
