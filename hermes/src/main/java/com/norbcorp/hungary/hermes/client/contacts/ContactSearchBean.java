package com.norbcorp.hungary.hermes.client.contacts;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

@Named("contactSearchBean")
@ViewScoped
public class ContactSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private String searchString;
	
	private List<String> results;
	
	@PostConstruct
	public void init(){
		results=new LinkedList<String>();
	}
	
	@Inject
	private XMPPConnectionManager xmppManager;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}
	
	public void search() throws NoResponseException, XMPPErrorException, NotConnectedException{
		results=xmppManager.userSearch(searchString);
	}
}
