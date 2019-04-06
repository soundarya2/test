package com.siriuscom.ipl_scorer.bundle.servlets;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.siriuscom.ipl_scorer.bundle.services.MatchOperationsImpl;

@Component(service = { Servlet.class },property = {
        "sling.servlet.resourceTypes=ipl-scorer/update-score-servlet",
         "sling.servlet.methods=POST", 
         "sling.servlet.methods=GET"
    })
public class UpdateScore extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3676204632666188652L;
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException {

		ResourceResolver resolver = req.getResourceResolver();
		
		String currentMatchPath = req.getParameter("currentMatchPath");
		String currentBowlerPath = req.getParameter("currentBowlerPath");
		String currentInnings = req.getParameter("currentInnings");
		String currentBatsmanPath = req.getParameter("currentBatsmanPath");
		String currentBattingTeamPath = req.getParameter("currentBattingTeamPath");
		String currentBowlingTeamPath = req.getParameter("currentBowlingTeamPath");
		Integer currentBall = Integer.parseInt(req.getParameter("currentBall"));
		Integer currentOver = Integer.parseInt(req.getParameter("currentOver"));
		Integer currentBallRun = Integer.parseInt(req.getParameter("currentBallRun"));
		String currentBallRunType = req.getParameter("currentBallRunType");
		boolean isWicket = Boolean.parseBoolean(req.getParameter("isWicket"));
		boolean isBallValid = Boolean.parseBoolean(req.getParameter("isBallValid"));
		Integer firstInningsTotal = Integer.parseInt(req.getParameter("firstInningsTotal"));
		Integer firstInningsWickets =Integer.parseInt(req.getParameter("firstInningsWickets"));
		Integer secondInningsTotal =Integer.parseInt(req.getParameter("secondInningsTotal"));
		Integer secondInningsWickets =Integer.parseInt(req.getParameter("secondInningsWickets"));
		Integer extras =Integer.parseInt(req.getParameter("extras"));
		String[] thisOver = req.getParameterValues("thisOver[]");
		Integer runsToBeUpdated = Integer.parseInt(req.getParameter("runsToBeUpdated"));
		
		MatchOperationsImpl matchOperations = new MatchOperationsImpl();
		try {
			matchOperations.updateScore(resolver, currentMatchPath, currentInnings , currentBowlerPath , currentBatsmanPath, currentBattingTeamPath , currentBowlingTeamPath, currentBall , currentOver , currentBallRun , currentBallRunType, isWicket , isBallValid, firstInningsTotal, firstInningsWickets , secondInningsTotal, secondInningsWickets, extras, thisOver , runsToBeUpdated);
		} catch (UnsupportedRepositoryOperationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}

}
