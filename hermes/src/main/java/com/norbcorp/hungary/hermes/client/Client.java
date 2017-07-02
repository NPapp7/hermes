package com.norbcorp.hungary.hermes.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

import org.jivesoftware.smack.XMPPException;

/**
 * ClientManager manages the data of connections of an xmpp network.
 * 
 * @author norbert
 *
 */
@Named("client")
@SessionScoped
public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String newPassword;
	private String newPasswordAgain;
	private String domain="nor-pc";
	static Logger logger = LogManager.getLogManager().getLogger(Client.class.getName());

	@Inject
	private XMPPConnectionManager xmppConnectionManager;
	
	public  String login() throws InterruptedException, XMPPException, SmackException, IOException{
		try {
			this.xmppConnectionManager.connectAndLogin(domain,userName, password);
			if(xmppConnectionManager.isAuthenticated()){
				logger.log(Level.INFO, "User "+userName+" logged with "+password);
				return "successful_login";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password or user name is not correct."));
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
		logger.log(Level.SEVERE, "New password: "+newPassword);
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
}
