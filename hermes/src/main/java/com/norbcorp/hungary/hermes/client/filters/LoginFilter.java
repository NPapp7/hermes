package com.norbcorp.hungary.hermes.client.filters;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.norbcorp.hungary.hermes.client.Client;


public class LoginFilter implements Filter{
	
	private static Logger logger = Logger.getLogger(LoginFilter.class.getName());
	
	@Inject
	private Client userBean;
	
	    /**
	     * Checks if user is logged in. If not it redirects to the login.xhtml page.
	     */
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	        // Get the loginBean from session attribute
	        Client loginBean = userBean;//= (UserBean)((HttpServletRequest)request).getSession().getAttribute("userBean");
	         
	        // For the first application request there is no loginBean in the session so user needs to log in
	        // For other requests loginBean is present but we need to check if user has logged in successfully
        	HttpServletRequest httpServletrequest = (HttpServletRequest) request;
        	String path = httpServletrequest.getRequestURI();
	        if ((loginBean == null || !loginBean.isUserLogged()) && path.contains("/user/")) {
	            String contextPath = ((HttpServletRequest)request).getContextPath();
	            ((HttpServletResponse)response).sendRedirect(contextPath + "/faces/login.xhtml");
	        }
	         
	        chain.doFilter(request, response);
	             
	    }
	 
	    public void init(FilterConfig config) throws ServletException {
	        // Nothing to do here!
	    }
	 
	    public void destroy() {
	        // Nothing to do here!
	    }   
	     
	}

