package com.norbcorp.hungary.hermes.client.connection;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.ByteUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.xdata.Form;

//import com.norbcorp.hungary.jsfwithxmpp.managedbean.application.ConnectionFactory.ChatManagerListenerImpl;


@Named("xmppCon")
@SessionScoped
public class XMPPConnectionManager implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(XMPPConnectionManager.class.getName());
	
	private final int PORT=5222;
	
	private static final int PACKET_REPLY_TIMEOUT= 100000;
	private XMPPTCPConnectionConfiguration.Builder config;
	private AbstractXMPPConnection connection = null;
	private Roster roster;
	//private ConnectionConfiguration connConfig; 
	
	PubSubManager pbmgr;
	
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
					.setPort(PORT).setUsernameAndPassword(userName, password).setDebuggerEnabled(false).build();
			this.connection = new XMPPTCPConnection(config.build());
			
			 try {
	                connection.setPacketReplyTimeout(10000);
	                connection.addConnectionListener(connectionListener);
	                connection.connect();
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
	
	public String logout() {
		if (connection != null) {
			connection.disconnect();
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			logger.info("The XMPP manager is disconnected");
		}
		return "logout";
	}
	
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
	 * @return List of user who match with the search string.
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
	
	
	/**
	 * 
	 * @param userJIDAndDomain 
	 * @throws Exception
	 */
	public void createEntry(String userJIDAndDomain) throws Exception {
		logger.info(String.format("Creating entry for buddy '%1$s", userJIDAndDomain));
		//ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListenerImpl());
		Roster.getInstanceFor(connection).createEntry(userJIDAndDomain, userJIDAndDomain.split("@")[0], null);
	//	this.chat = ChatManager.getInstanceFor(connection).createChat(userJIDAndDomain, myMessageListener);
	//	logger.info("Chat is initiated: " + (chat == null ? "No" : "Yes"));
	}
}
