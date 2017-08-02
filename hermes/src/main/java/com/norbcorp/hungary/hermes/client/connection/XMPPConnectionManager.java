package com.norbcorp.hungary.hermes.client.connection;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.callback.CallbackHandler;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.ByteUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import com.norbcorp.hungary.hermes.client.Client;
import com.norbcorp.hungary.hermes.client.contacts.Contact;
import com.norbcorp.hungary.hermes.client.contacts.ContactMessage;
import com.norbcorp.hungary.hermes.client.contacts.Conversation;
import com.norbcorp.hungary.hermes.client.groupchat.ChatRoom;
import com.norbcorp.hungary.hermes.client.groupchat.GroupChatListener;
import com.norbcorp.hungary.hermes.client.messaging.listener.HermesChatMessageListener;

/**
 * 
 * 
 * @author nor
 *
 */
@Named("xmppCon")
@SessionScoped
public class XMPPConnectionManager implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(XMPPConnectionManager.class.getName());
	
	private final int PORT=5222;
	
	/**
	 * Utility class for sending presence message to the contacts
	 */
	private Presence presence;


	/**
	 * Key-value pairs of String and Conversation entries.
	 * 
	 * Key is the name of the user. Valus is <i>Conversation</i> instance which contains information about the message and time when it was sent.
	 */
	private HashMap<String, Conversation> chatHistory = new HashMap<String, Conversation>();
	
	private static final int PACKET_REPLY_TIMEOUT= 500000;
	private XMPPTCPConnectionConfiguration.Builder config;
	private AbstractXMPPConnection connection = null;
	private Roster roster;
	private Chat chat; 
	
	private ChatMessageListener myMessageListener = new HermesChatMessageListener(chatHistory);
	
	PubSubManager pbmgr;
	
	@Inject
	private Client client;
	
	public void sendMessage(String message, String userJIDAndDomain) throws Exception {
		try {
			if (chat != null && chat.getParticipant().equalsIgnoreCase(userJIDAndDomain)) {
				logger.warning("Chat with "+chat.getParticipant());
				Message customMessage = new Message(userJIDAndDomain, message);
				chat.sendMessage(customMessage);
				// Store the message
				if (!chatHistory.containsKey(userJIDAndDomain)) {
					List<ContactMessage> messages = new LinkedList<ContactMessage>();
					messages.add(new ContactMessage(userJIDAndDomain, message, Instant.now()));
					chatHistory.put(userJIDAndDomain, new Conversation(messages));
				} else {
					chatHistory.get(userJIDAndDomain).getMessages()
							.add(new ContactMessage(client.getUserName(), message, Instant.now()));
				}
				logger.info("Participant is "+chat.getParticipant());

			} else {
				createEntry(userJIDAndDomain);
				sendMessage(message,userJIDAndDomain);
			}
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connect and login to a XMPP network,
	 * 
	 * @param server name of server or domain
	 * @param userName name of the current user
	 * @param password password of the current user
	 * @throws InterruptedException
	 * @throws XMPPException
	 * @throws SmackException
	 * @throws IOException
	 */
	public  void connectAndLogin(final String server, final String userName, final String password) throws InterruptedException, XMPPException, SmackException, IOException{
		logger.info(String.format("Initializing connection to server: %1$s port %2$2d", server, PORT));
		if (connection == null || !connection.isConnected() || !connection.isAuthenticated()) {
			SmackConfiguration.setDefaultPacketReplyTimeout(PACKET_REPLY_TIMEOUT);
			ConnectionListener connectionListener = new ConnectionListener() {
		        @Override
		        public void connected(XMPPConnection xmppConnection) {
		            logger.info("xmpp connected");
		            try {
		                SASLAuthentication.registerSASLMechanism(new SASLMechanism() {
		                	
		                    @Override
		                    protected void authenticateInternal(CallbackHandler callbackHandler) throws SmackException {
		                    }

		                    @Override
		                    protected byte[] getAuthenticationText() throws SmackException {
		                        byte[] authcid = toBytes('\u0000' + this.authenticationId);
		                        byte[] passw = toBytes('\u0000' + this.password);
		                        return ByteUtils.concact(authcid, passw);
		                    }

		                    @Override
		                    public String getName() {
		                        return "PLAIN";
		                    }

		                    @Override
		                    public int getPriority() {
		                        return 410;
		                    }

		                    @Override
		                    public void checkIfSuccessfulOrThrow() throws SmackException {

		                    }

		                    @Override
		                    protected SASLMechanism newInstance() {
		                        return this;
		                    }
		                });
		                connection.login();
		            } catch (XMPPException e) {
		                e.printStackTrace();
		            } catch (SmackException e) {
		                e.printStackTrace();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }

		        @Override
		        public void authenticated(XMPPConnection xmppConnection, boolean b) {
		            logger.info("xmpp authenticated");
		        }
		        
		        @Override
		        public void connectionClosed() {
		            logger.info("xmpp connection closed");
		        }

		        @Override
		        public void connectionClosedOnError(Exception e) {
		            logger.info("xmpp cononection closed on error");
		        }

		        @Override
		        public void reconnectionSuccessful() {
		            logger.info("xmpp reconnection successful");
		        }

		        @Override
		        public void reconnectingIn(int i) {
		            logger.info("xmpp reconnecting in " + i);
		        }

		        @Override
		        public void reconnectionFailed(Exception e) {
		            logger.info("xmpp reconnection failed");
		        }
		    };
			config = XMPPTCPConnectionConfiguration.builder();
			config.setCompressionEnabled(true).setSendPresence(true).setSecurityMode(SecurityMode.disabled).setServiceName(server).setHost(server)
					.setPort(PORT).setUsernameAndPassword(userName, password).setDebuggerEnabled(true).build();
			this.connection = new XMPPTCPConnection(config.build());
			
			 try {
	                connection.setPacketReplyTimeout(10000);
	                connection.addConnectionListener(connectionListener);
	                connection.connect();
	          /*      Roster roster = Roster.getInstanceFor(connection);
	    			roster.addRosterListener(new HermesRosterListener());*/
	               
	            } catch (SmackException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (XMPPException e) {
	                e.printStackTrace();
	            }
			logger.info(String.format("Connection is "+(this.connection.isConnected()==true ? "connect." : "not connected.")));
		}
	}
	
	/**
	 * Invalidates the session and disconnect from the server.
	 * 
	 * @return <i>String</i> which is used for navigation.
	 */
	public String logout() {
		if (connection != null) {
			connection.disconnect();
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			logger.info("The XMPP manager is disconnected");
		}
		return "logout";
	}
	
	/**
	 * Change password the logged user.
	 * 
	 * @param newPassword of user. 
	 */
	public void changePassword(String newPassword){
		try {
			AccountManager accountManager = AccountManager.getInstance(connection);
			accountManager.sensitiveOperationOverInsecureConnection(true);
			accountManager.changePassword(newPassword);
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password change is not supported on this server."));
			e.printStackTrace();
		}
	}

	public Boolean isConnected(){
		return connection.isConnected();
	}
	
	/**
	 * @return true if the current connection is properly authenticated
	 */
	public Boolean isAuthenticated(){
		return connection == null ? false : connection.isAuthenticated();
	}
	
	/**
	 * List of users who are in the roster of logged user.
	 * 
	 * @return <i>Set</i> of contacts of logged user.
	 * @throws NotLoggedInException
	 * @throws NotConnectedException
	 * @throws InterruptedException
	 */
	public Set<RosterEntry> getRosters() throws NotLoggedInException, NotConnectedException, InterruptedException {
		Roster roster = Roster.getInstanceFor(connection);
		this.roster=roster;
		if (!roster.isLoaded())
			roster.reloadAndWait();
		return roster.getEntries();
	}

	public Roster getRoster() {
		if(roster==null && connection!=null){
			roster=Roster.getInstanceFor(connection);
		}
		return roster;
	}

	public void setRoster(Roster roster) {
		this.roster = roster;
	}
	
	public void sendStanza(Stanza stanza){
		try {
			this.connection.sendStanza(stanza);
		} catch (NotConnectedException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sendCustomStanza(Stanza stanza) {
		try {
			this.connection.sendStanza(stanza);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param search: <i>String</i>  
	 * 
	 * @return List of users who match with the search string.
	 * @throws NoResponseException
	 * @throws XMPPErrorException
	 * @throws NotConnectedException
	 */
	public List<String> userSearch(String search) throws NoResponseException, XMPPErrorException, NotConnectedException{
		UserSearchManager userSearchManager = new UserSearchManager(connection);
		Form searchForm = userSearchManager.getSearchForm("search." + connection.getServiceName());
		Form answerForm = searchForm.createAnswerForm();
		answerForm.setAnswer("Name", true);
		answerForm.setAnswer("search", search);
		
		org.jivesoftware.smackx.search.ReportedData resultData;
		resultData = userSearchManager.getSearchResults(answerForm, "search."+ connection.getServiceName());
		List<String> result=new LinkedList<String>();
		for(Iterator<Row> it=resultData.getRows().iterator();it.hasNext();){
			result.add(it.next().getValues("Name").get(0));
		}
		return result;
	}
	
	public HashMap<String, Conversation> getChatHistory() {
		return chatHistory;
	}

	public void setChatHistory(HashMap<String, Conversation> chatHistory) {
		this.chatHistory = chatHistory;
	}

	private class ChatManagerListenerImpl implements ChatManagerListener {

		/** {@inheritDoc} */
		@Override
		public void chatCreated(final Chat chat, final boolean createdLocally) {
			chat.addMessageListener(myMessageListener);
		}

	}

	/**
	 * Communication history between the current user and the selected user.
	 * 
	 * @param userName name of the user
	 * @return <i>List</i> of <i>ContactMessage</i> entries.
	 */
	public List<ContactMessage> getHistoryByName(String userName) {
		if (userName != null) {
			userName+="@"+client.getDomain();
			if (chatHistory.get(userName) != null)
				return chatHistory.get(userName).getMessages();
			else {
				chatHistory.put(userName, new Conversation(new LinkedList<ContactMessage>()));
				return chatHistory.get(userName).getMessages();
			}
		} else
			return new LinkedList<ContactMessage>();
	}
	
	/**
	 * Create new roster entry for the given user.
	 * 
	 * @param userJIDAndDomain 
	 * @throws Exception
	 */
	public void createEntry(String userJIDAndDomain) throws Exception {
		logger.info(String.format("Creating entry for buddy '%1$s", userJIDAndDomain));
		ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListenerImpl());
		Roster.getInstanceFor(connection).createEntry(userJIDAndDomain, userJIDAndDomain.split("@")[0], null);
		this.chat = ChatManager.getInstanceFor(connection).createChat(userJIDAndDomain, myMessageListener);
		logger.info("Chat is initiated: " + (chat == null ? "No" : "Yes"));
	}
	
	/**
	 * Loads information of every member of roster. Remove domain from their names.
	 * 
	 * @return <i>List</i> of contacts.
	 * @throws NotLoggedInException
	 * @throws NotConnectedException
	 * @throws InterruptedException
	 */
	public List<Contact> getContacts() throws NotLoggedInException, NotConnectedException, InterruptedException {
		List<Contact> contacts=new LinkedList<Contact>();
		Roster roster = getRoster();
		roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
		roster.reload();
		int id = 0;
		for (RosterEntry entry : getRosters()) {
			if (!(client.getUserName().equalsIgnoreCase(entry.getUser().split("@")[0]))) {
				Contact contact = new Contact();
				presence = roster.getPresence(entry.getUser());
				id += 1;
				contact.setId(id);
				contact.setContactName(entry.getUser().split("@")[0]);
				contact.setContactPresenceMode(presence.getMode().toString());
				contact.setPresenceType(presence.getType().name());
				contact.setPresenceStatus(presence.isAvailable());
				contact.setPresenceTextStatus(presence.getStatus());

				if (contacts.contains(contact)) {
					contacts.get(contacts.indexOf(contact)).setPresenceStatus(contact.isPresenceStatus());
				} else
					contacts.add(contact);
			}
			//Set presence text of client user
			else{
				client.setPresenceText(roster.getPresence(entry.getUser()).getStatus());
			}
		}
		return contacts;
	}
	
	/**
	 * Returns a collection of server supported multi user chat services.
	 * 
	 * @return a Collection of multi user services supported by the server.
	 */
	public List<String> getSupportedServices(){
		MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(this.connection);
		try {
			return multiUserChatManager.getServiceNames();
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param roomName name of the room
	 * @param subject
	 * @param listOfUsers
	 * @throws XMPPErrorException
	 * @throws NoResponseException
	 * @throws NotConnectedException
	 */
	public MultiUserChat createMultiUserChatRoom(String roomName,String subject, List<String> listOfUsers) throws XMPPErrorException, NoResponseException, NotConnectedException{
		MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(this.connection);
		multiUserChatManager.addInvitationListener(new GroupChatListener());
		MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(roomName+'@'+"conference.nor-pc");
		try {
			logger.info("ChatRoom is successfully created: "+multiUserChat.createOrJoin(this.client.getUserName(),"",new DiscussionHistory(),200000)+"");
			multiUserChat.changeSubject(subject);
			for(String user : listOfUsers){
				multiUserChat.invite(user+'@'+this.client.getDomain(), "");
				multiUserChat.grantOwnership(user+'@'+this.client.getDomain());
			}
			return multiUserChat;
		} catch (NoResponseException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param message
	 * @param chatRoom
	 */
	public void sendMessageToChatRoom(String message, ChatRoom chatRoom){
		try {
			chatRoom.getMultiUserChat().sendMessage(message);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
