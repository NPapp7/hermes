package com.norbcorp.hungary.hermes.client.contacts.listeners;

import java.util.Collection;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

/**
 * Listener of roster changes.
 * 
 * @author nor
 *
 */
public class HermesRosterListener implements RosterListener{

	private static Logger logger = Logger.getLogger(HermesRosterListener.class.getName());
	
	@Override
	public void entriesAdded(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void presenceChanged(Presence presence) {
		logger.info(presence+"");
		//if(presence!=null && presence.getFrom()!=null)
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Presence changed "+presence.getFrom()+" presence"));
		logger.info("Presence changed "+presence.getFrom()+" presence to "+presence.getStatus());
	}

}
