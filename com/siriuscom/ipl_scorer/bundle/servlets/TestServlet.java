package com.siriuscom.ipl_scorer.bundle.servlets;

import javax.servlet.Servlet;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(service = { Servlet.class }, property = { "sling.servlet.resourceTypes=ipl-scorer/test-servlet",
		"sling.servlet.methods=POST" })
public class TestServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


 
	
	
	

}
