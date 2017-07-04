package com.norbcorp.hungary.hermes.client.contacts;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;


@Named
@ViewScoped
public class ContactBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ContactBean.class.getName());
	
	/**
	 * List of users who are online.
	 */
	private List<Contact> contacts;
	private Contact selectedContact;

	/**
	 * Presence information
	 */
	private String presenceInfoMessage;

	/**
	 * Property for creating roster entry.
	 */
	private String userJID;

	/**
	 * Utility class for sending presence message to the contacts
	 */
	private Presence presence;

	/**
	 * Presence mode
	 */
	private Presence.Mode selectedMode;

	// Presence modes to select from
	private Presence.Mode presenceAvailable = Presence.Mode.available;
	private Presence.Mode presenceAway = Presence.Mode.away;
	private Presence.Mode presenceChat = Presence.Mode.chat;

	private ResourceBundle resourceBundle;
	
	@Inject
	private XMPPConnectionManager xmppManager;

	@PostConstruct
	public void init() {
		contacts = new LinkedList<Contact>();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String messageBundleName = facesContext.getApplication().getMessageBundle();
		Locale locale = facesContext.getViewRoot().getLocale();
		resourceBundle = ResourceBundle.getBundle(messageBundleName, locale);
	}

	public List<Contact> getContacts() throws NotLoggedInException, NotConnectedException, InterruptedException {
		Roster roster = xmppManager.getRoster();
		roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
		roster.reload();
		int id = 0;
		for (RosterEntry entry : xmppManager.getRosters()) {
			Contact c = new Contact();
			presence = roster.getPresence(entry.getUser());
			id += 1;
			c.setId(id);
			c.setContactName(entry.getUser());
			if(presence.getMode()==presence.getMode().available)
				c.setContactPresenceMode(resourceBundle.getString("contact.available") == null ? presence.getMode().name() : resourceBundle.getString("contact.available"));
			else if(presence.getMode() == presence.getMode().away)
				c.setContactPresenceMode(resourceBundle.getString("contact.away") == null ? presence.getMode().name() : resourceBundle.getString("contact.away"));
			else if(presence.getMode() == presence.getMode().chat)
				c.setContactPresenceMode(resourceBundle.getString("contact.away") == null ? presence.getMode().name() : resourceBundle.getString("contact.away"));
			
			c.setPresenceType(presence.getType().name());
			c.setPresenceStatus(presence.isAvailable());
			c.setPresenceTextStatus(presence.getStatus());

			if (contacts.contains(c)) {
				contacts.get(contacts.indexOf(c)).setPresenceStatus(c.isPresenceStatus());
			} else
				contacts.add(c);
		}
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	/**
	 * Create roster entry with domain name.
	 * 
	 * @throws Exception
	 */
	public void createEntry() throws Exception {
		if(this.userJID!=null && !(this.userJID.equals("")))
			this.xmppManager.createEntry(this.userJID + "@nor-pc");
		else
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select an user first"));
		this.contacts=this.getContacts();
	}

	public String getUserJID() {
		return userJID;
	}

	public void setUserJID(String userJID) {
		this.userJID = userJID;
	}

	public Contact getSelectedContact() {
		return selectedContact;
	}

	public void setSelectedContact(Contact selectedContact) {
		this.selectedContact = selectedContact;
	}

	public void deleteRosterEntry(String username)
			throws NotLoggedInException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
		RosterEntry re = xmppManager.getRoster().getEntry(username);
		xmppManager.getRoster().removeEntry(re);
		this.contacts=this.getContacts();
	}

	public String getPresenceInfoMessage() {
		return presenceInfoMessage;
	}

	public void setPresenceInfoMessage(String presenceInfoMessage) {
		this.presenceInfoMessage = presenceInfoMessage;
	}

	public void sendCustomStanza(Stanza stanza) {
		this.xmppManager.sendStanza(stanza);
	}
	
	/**
	 * Sends the presence to the rosters of other users.
	 */
	public void sendPresence() {
		this.presence = new Presence(Presence.Type.available, presenceInfoMessage, 1, this.selectedMode);
		this.xmppManager.sendCustomStanza(presence);
	}

	public Presence.Mode getSelectedMode() {
		return selectedMode;
	}

	public void setPresenceAvailable(Presence.Mode presenceAvailable) {
		this.presenceAvailable = presenceAvailable;
	}

	public void setPresenceAway(Presence.Mode presenceAway) {
		this.presenceAway = presenceAway;
	}

	public void setPresenceChat(Presence.Mode presenceChat) {
		this.presenceChat = presenceChat;
	}

	public void setSelectedMode(Presence.Mode selectedMode) {
		this.selectedMode = selectedMode;
	}

	public Presence.Mode getPresenceAvailable() {
		return presenceAvailable;
	}

	public Presence.Mode getPresenceAway() {
		return presenceAway;
	}

	public Presence.Mode getPresenceChat() {
		return presenceChat;
	}
}
