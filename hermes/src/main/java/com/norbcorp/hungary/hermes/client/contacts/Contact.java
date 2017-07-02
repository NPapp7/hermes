package com.norbcorp.hungary.hermes.client.contacts;

import java.io.Serializable;

public class Contact implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private boolean presenceStatus;
	/**
	 * Row id which is used for selection table
	 */
	private int id=0;
	private String presenceType;
	private String contactName;
	private String contactPresenceMode;
	private String presenceTextStatus;
	
	public Contact() {
		super();
	}
	
	public Contact(boolean presenceStatus, String presenceType, String contactName, String contactPresenceMode) {
		super();
		this.presenceStatus = presenceStatus;
		this.presenceType = presenceType;
		this.contactName = contactName;
		this.contactPresenceMode = contactPresenceMode;
	}

	public String getPresenceType() {
		return presenceType;
	}
	public void setPresenceType(String presenceType) {
		this.presenceType = presenceType;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public boolean isPresenceStatus() {
		return presenceStatus;
	}

	public void setPresenceStatus(boolean presenceStatus) {
		this.presenceStatus = presenceStatus;
	}

	public String getContactPresenceMode() {
		return contactPresenceMode;
	}

	public void setContactPresenceMode(String contactPresenceMode) {
		this.contactPresenceMode = contactPresenceMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactName == null) ? 0 : contactName.hashCode());
		result = prime * result + ((contactPresenceMode == null) ? 0 : contactPresenceMode.hashCode());
		result = prime * result + (presenceStatus ? 1231 : 1237);
		result = prime * result + ((presenceType == null) ? 0 : presenceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (contactName == null) {
			if (other.contactName != null)
				return false;
		} else if (!contactName.equals(other.contactName))
			return false;
		return true;
	}
	
	public String getPresenceTextStatus() {
		return presenceTextStatus;
	}

	public void setPresenceTextStatus(String presenceTextStatus) {
		this.presenceTextStatus = presenceTextStatus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
