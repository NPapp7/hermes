package com.norbcorp.hungary.hermes.client.geolocation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

@Named("userSearch")
@ViewScoped
public class UserSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private String searchString;
	
	private List<String> results=new LinkedList<String>();
	
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
	
	public void search() {
		try {
			results=xmppManager.userSearch(searchString);
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
