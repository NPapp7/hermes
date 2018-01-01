package com.norbcorp.hungary.hermes.client.geolocation;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

@Named("geoloc")
@ViewScoped
public class GeolocationBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(GeolocationBean.class.getName());
	
	private double lat;
	private double lng;
	private String mapAddress;
	private String text;
	private String itemId;
	private String nodeName;
	
	@Inject
	private XMPPConnectionManager xmppManager;
	
	public void onPointSelect(ActionEvent event) {
	        FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Point Selected", "Lat:" + getLat() + ", Lng:" + getLng() + " Address: " + getMapAddress()));
	}
	
	public void publishItemWithGeolocation()
	{
		String xml ="<geoloc xmlns='http://jabber.org/protocol/geoloc'>"
				+ "<lat>"+lat+"</lat><lon>"+lng+"</lon>"
						+ "<locality>"+mapAddress+"</locality>"
						+ "<text>"+text+"</text></geoloc>";
		try {
			xmppManager.sendItemGeolocPayload(nodeName, itemId, xml);
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getMapAddress() {
		return mapAddress;
	}

	public void setMapAddress(String mapAddress) {
		this.mapAddress = mapAddress;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
}
