package com.siriuscom.ipl_scorer.bundle.servlets;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.siriuscom.ipl_scorer.bundle.services.MatchOperationsImpl;

@Component(service = { Servlet.class },property = {
        "sling.servlet.resourceTypes=ipl-scorer/update-current-bowler-servlet",
         "sling.servlet.methods=POST", 
         "sling.servlet.methods=GET"
    })
public class UpdateBowler extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7421083733042150127L;
	
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException {
		String bowlerId = req.getParameter("currentBowlerId");
		String currentMatchPath = req.getParameter("currentMatchPath");
		String currentBowlerTeamPath = req.getParameter("currentBowlingTeamPath");
		System.out.println("IN DOPOST");
		MatchOperationsImpl matchOperations = new MatchOperationsImpl();
		try {
			matchOperations.updateCurrentBowler(req.getResourceResolver(), bowlerId, currentMatchPath, currentBowlerTeamPath);
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
}
