package com.norbcorp.hungary.hermes.client.converters.object;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import com.norbcorp.hungary.hermes.client.contacts.Contact;
import com.norbcorp.hungary.hermes.client.messaging.MessagingBean;


@Named
@ApplicationScoped
public class ContactConverter implements Converter{

	private static final long serialVersionUID = 1L;

	@Inject
	private MessagingBean messagingBean;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value==null || value.isEmpty())
			return null;
		try{
			Contact selectedContact=new Contact(value);
			if(messagingBean.getListOfAvailableContacts().contains(selectedContact))
				return messagingBean.getListOfAvailableContacts().get(messagingBean.getListOfAvailableContacts().indexOf(selectedContact));
			else
				return null;
				
		}catch(Exception e){
			throw new ConverterException(new FacesMessage("Not valid id:"+value),e);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value==null)
			return null;
		if(value instanceof Contact){
			return ((Contact)value).getContactName();
		}
		return null;
	}
}
