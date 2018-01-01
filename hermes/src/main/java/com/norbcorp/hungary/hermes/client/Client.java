package com.norbcorp.hungary.hermes.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.XMPPException;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;
import com.norbcorp.hungary.hermes.client.groupchat.GroupChatInvitation;

/**
 * Client bean manages the data of connections of an xmpp network.
 * 
 * @author norbert
 *
 */
@Named("client")
@SessionScoped
public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the logged user.
	 */
	private String userName;
	
	/**
	 * Password of logger the user.
	 */
	private String password;
	
	private String newPassword;
	private String newPasswordAgain;
	
	private String newTopicName = "";
	private String topicToSubscribe = "";
	private String nameOfTheNode = "";
	private String contentOfPayLoad = "";
	private String newNodeString = "";
	private String chosenSubscription;
	
	/**
	 * Name of the new leaf node which can be created on the HomePage.
	 */
	private String newNodeName="";
	
	private SelectItem[] subscripedOptions=new SelectItem[0];
	
	/**
	 * Name of the server or domain.
	 */
	private String domain;
	static Logger logger = LogManager.getLogManager().getLogger(Client.class.getName());
	
	/**
	 * Current presence text which can be modified by the user.
	 */
	private String presenceText;

	Locale browserLocale;
	
	@Inject
	private XMPPConnectionManager xmppConnectionManager;
	
	private LinkedList<GroupChatInvitation> groupChatInvitations = new LinkedList<GroupChatInvitation>();
	
	/**
	 * Login method logs the user in with the given domain, user name and password.
	 * 
	 * @return String as a navigation parameter.
	 * @throws InterruptedException
	 * @throws XMPPException
	 * @throws SmackException
	 * @throws IOException
	 */
	public String login() throws InterruptedException, XMPPException, SmackException, IOException{
		try {
			this.xmppConnectionManager.connectAndLogin(domain, userName, password);
			if(xmppConnectionManager.isAuthenticated()){
				logger.log(Level.INFO, "User "+userName+" logged with "+password);
				return "successful_login";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password, user name or domain is not correct."));
			}
		} catch (NotLoggedInException nlie) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details are not correct please try again!"));
		}
		catch(UnknownHostException uhe){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Unknown host!"));
		}
		return "not_successful_login";
	}
	
	public void changePassword(){
		if(newPassword.equals(newPasswordAgain)){
			xmppConnectionManager.changePassword(newPassword);
		}
		else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Passwords are not the same."));
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public XMPPConnectionManager getXmppConnectionManager() {
		return xmppConnectionManager;
	}

	public void setXmppConnectionManager(XMPPConnectionManager xmppConnectionManager) {
		this.xmppConnectionManager = xmppConnectionManager;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String logout(){
		this.userName=null;
		this.password=null;
		this.xmppConnectionManager.logout();
		return "logout";
	}
	
	public Boolean isUserLogged(){
		return xmppConnectionManager.isAuthenticated();
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}

	public void setNewPasswordAgain(String newPasswordAgain) {
		this.newPasswordAgain = newPasswordAgain;
	}

	public Locale getBrowserLocale() {
		return browserLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	}

	public void setBrowserLocale(Locale browserLocale) {
		this.browserLocale = browserLocale;
	}

	public String getPresenceText() {
		return presenceText;
	}

	public void setPresenceText(String presenceText) {
		this.presenceText = presenceText;
	}

	public LinkedList<GroupChatInvitation> getGroupChatInvitations() {
		return groupChatInvitations;
	}

	public void setGroupChatInvitations(LinkedList<GroupChatInvitation> groupChatInvitations) {
		this.groupChatInvitations = groupChatInvitations;
	}
	
	
	public String getNewNodeName() {
		return newNodeName;
	}

	public void setNewNodeName(String newNodeName) {
		this.newNodeName = newNodeName;
	}

	public String getChosenSubscription() {
		return chosenSubscription;
	}
	
	public void setChosenSubscription(String chosenSubscription) {
		this.chosenSubscription = chosenSubscription;
	}


	public String getNameOfTheNode() {
		return nameOfTheNode;
	}

	public void setNameOfTheNode(String nameOfTheNode) {
		this.nameOfTheNode = nameOfTheNode;
	}

	public void createTopic() throws NoResponseException, XMPPErrorException, NotConnectedException {
		xmppConnectionManager.createNode(newTopicName);
	}
	
	public void createLeafNode() throws NoResponseException, XMPPErrorException, NotConnectedException{
		xmppConnectionManager.createNode(this.newNodeName);
	}
	
	public String getNewNodeString() {
		return newNodeString;
	}

	public void setNewNodeString(String newNodeString) {
		this.newNodeString = newNodeString;
	}

	/**
	 * 
	 * @throws NoResponseException
	 * @throws XMPPErrorException
	 * @throws NotConnectedException
	 */
	public void sendItem(String nodeName, String namespace)
			throws NoResponseException, XMPPErrorException, NotConnectedException {
		this.xmppConnectionManager.sendItem(newNodeString, nodeName, namespace, contentOfPayLoad);
	}
	
	public void sendItem() throws NoResponseException, XMPPErrorException, NotConnectedException{
		this.xmppConnectionManager.sendItem(newNodeString,this.nameOfTheNode,"",contentOfPayLoad);
	}

	public String getTopicToSubscribe() {
		return topicToSubscribe;
	}

	public void setTopicToSubscribe(String topicToSubscribe) {
		this.topicToSubscribe = topicToSubscribe;
	}

	public void subscribeToTopic() throws NoResponseException, XMPPErrorException, NotConnectedException {
		this.xmppConnectionManager.subscribe(topicToSubscribe);
	}

	public String getNewTopicName() {
		return newTopicName;
	}

	public void setNewTopicName(String newTopicName) {
		this.newTopicName = newTopicName;
	}

	public SelectItem[] getSubscripedOptions() {
		return subscripedOptions;
	}

	public void setSubscripedOptions(SelectItem[] subscripedOptions) {
		this.subscripedOptions = subscripedOptions;
	}
	
	
}
