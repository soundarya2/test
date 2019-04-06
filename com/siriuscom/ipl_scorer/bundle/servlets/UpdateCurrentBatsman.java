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
        "sling.servlet.resourceTypes=ipl-scorer/update-current-batsman-servlet",
         "sling.servlet.methods=POST", 
         "sling.servlet.methods=GET"
    })
public class UpdateCurrentBatsman extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5332201833810281825L;
	
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException {
		String batsmanId = req.getParameter("currentBatsmanId");
		String currentMatchPath = req.getParameter("currentMatchPath");
		String currentBattingTeamPath = req.getParameter("currentBattingTeamPath");
		MatchOperationsImpl matchOperations = new MatchOperationsImpl();
		try {
			matchOperations.updateCurrentBatsman(req.getResourceResolver(), batsmanId, currentMatchPath, currentBattingTeamPath);
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

}
