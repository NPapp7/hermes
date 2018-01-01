package com.norbcorp.hungary.hermes.client.geolocation;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.w3c.dom.Document;

import com.norbcorp.hungary.hermes.client.connection.XMPPConnectionManager;

@Named
@ViewScoped
public class EventsBean implements Serializable{

	private Logger logger = Logger.getLogger(EventsBean.class.getName());
	
	private static final long serialVersionUID = 1L;

	private MapModel emptyModel = new DefaultMapModel();
     
    private String title;
      
    private double lat;
      
    private double lng;
    
    private String locality;
	private String subscripedNode="";
	
	private List<Item> itemsOfLeafNodes=new LinkedList<Item>();
	
    /**
     *  Value of the items dropdowns.
     */
	private SelectItem[] subscripedOptions=new SelectItem[0];
  
	private List<org.jivesoftware.smackx.disco.packet.DiscoverItems.Item> items=new LinkedList<org.jivesoftware.smackx.disco.packet.DiscoverItems.Item>();
	
	@Inject
	private XMPPConnectionManager xmppManager;
    
    
	/**
	 * Get all of the item nodes to which the user subscribed.
	 * 
	 * @return
	 * @throws NoResponseException
	 * @throws XMPPErrorException
	 * @throws NotConnectedException
	 */
	public SelectItem[] getSubscripedOptions() throws NoResponseException, XMPPErrorException, NotConnectedException {
		this.subscripedOptions=new SelectItem[xmppManager.getSubscriptions().size()];
		int i=0;
		for(Subscription node : xmppManager.getSubscriptions()){
			subscripedOptions[i]=new SelectItem(node.getNode());
			i++;
		}
		return subscripedOptions;
	}
    
	public void setSubscripedOptions(SelectItem[] subscripedOptions) {
		this.subscripedOptions = subscripedOptions;
	}
	
	/**
	 * List of processed events containing every necessary information.
	 */
	private List<EventItem> eventItems=new LinkedList<EventItem>();
	
	public List<EventItem> getEventItems() {
		if(subscripedNode!=null && !subscripedNode.equals("")){
			itemsOfLeafNodes=xmppManager.getItemWithPayload(subscripedNode);
		}
		for(Item item : itemsOfLeafNodes){
			EventItem ei=new EventItem();
			ei.setId(item.getId());
			
		}
		return eventItems;
	}

	public void setEventItems(List<EventItem> eventItems) {
		this.eventItems = eventItems;
	}

	public List<Item> getItemsOfLeafNodes() {
		
		if(subscripedNode!=null && !subscripedNode.equals("")){
			itemsOfLeafNodes=xmppManager.getItemWithPayload(subscripedNode);
		}
		return itemsOfLeafNodes;
	}
	
	public void subscriptionNodeName(){
		try{
		itemsOfLeafNodes=getItemsOfLeafNodes();
		eventItems=new LinkedList<EventItem>();
		this.emptyModel=new DefaultMapModel();
		for(Item item : itemsOfLeafNodes){
			DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream input =  new ByteArrayInputStream(item.toXML().getBytes("UTF-8"));
			Document doc = builder.parse(input);
			this.lng=Double.valueOf(doc.getDocumentElement().getElementsByTagName("lon").item(0).getTextContent()).doubleValue();
			this.lat=Double.valueOf(doc.getDocumentElement().getElementsByTagName("lat").item(0).getTextContent()).doubleValue();
			this.title=doc.getDocumentElement().getElementsByTagName("text").item(0).getTextContent();
			
			//Add a new event item
			EventItem ei=new EventItem();
			ei.setId(item.getId());
			ei.setLat(String.valueOf(lat));
			ei.setLng(String.valueOf(lng));
			ei.setText(title);
			eventItems.add(ei);
			logger.info("Lng:"+lng);
			logger.info("Lat:"+lat);
			logger.info("Title:"+title);
			addMarker();
		}
		}
		catch (Exception e) {
			logger.warning("XML parsing error:");
			e.printStackTrace();
		}
	}

	public String getSubscripedNode() {
		return subscripedNode;
	}

	public void setSubscripedNode(String subscripedNode) {
		this.subscripedNode = subscripedNode;
	}

	public void setItemsOfLeafNodes(List<Item> itemsOfLeafNodes) {
		this.itemsOfLeafNodes = itemsOfLeafNodes;
	}

	
    public MapModel getEmptyModel() {
        return emptyModel;
    }
      
    public String getTitle() {
        return title;
    }
  
    public void setTitle(String title) {
        this.title = title;
    }
  
    public double getLat() {
        return lat;
    }
    
	public List<org.jivesoftware.smackx.disco.packet.DiscoverItems.Item> getItems() {
		return items;
	}


	public void setItems(List<org.jivesoftware.smackx.disco.packet.DiscoverItems.Item> items) {
		this.items = items;
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
      
    
    public void setEmptyModel(MapModel emptyModel) {
		this.emptyModel = emptyModel;
	}
	
	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void addMarker() {
        Marker marker = new Marker(new LatLng(lat, lng), title);
        emptyModel.addOverlay(marker);
          
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", "Lat:" + lat + ", Lon:" + lng));
    }
	
	
}
