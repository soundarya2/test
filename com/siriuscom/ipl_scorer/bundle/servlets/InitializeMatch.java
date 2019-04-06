package com.siriuscom.ipl_scorer.bundle.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.siriuscom.ipl_scorer.bundle.services.MatchOperationsImpl;

@Component(service = { Servlet.class },property = {
        "sling.servlet.resourceTypes=ipl-scorer/initialize-match-servlet",
         "sling.servlet.methods=POST", 
         "sling.servlet.methods=GET"
    })
public class InitializeMatch extends SlingAllMethodsServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5951167879298337964L;
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException, IOException {
		System.out.println("IN DO POST ::");
		MatchOperationsImpl matchOperation = new MatchOperationsImpl();
		try {
			matchOperation.inializeMatch(req.getResourceResolver());
			Node currentMatch = matchOperation.getCurrentMatch(req.getResourceResolver());
			resp.getWriter().write(currentMatch.getPath());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	
		System.out.println("testPostData :: " + req.getParameter("testPostData"));
	}
	

}
