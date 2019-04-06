package com.siriuscom.ipl_scorer.bundle.servlets;

import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.siriuscom.ipl_scorer.bundle.services.CreateMatchImpl;

@Component(service = { Servlet.class },property = {
        "sling.servlet.resourceTypes=ipl-scorer/create-match-servlet",
         "sling.servlet.methods=POST"
    })
public class CreateMatchServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6599508246020733692L;
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException{
		
		ResourceResolver resolver = req.getResourceResolver();
		//getting data from js
		String teamOne = req.getParameter("teamOne");
		String teamTwo = req.getParameter("teamTwo");
		String rules = req.getParameter("rules");
		String overs = req.getParameter("overs");
		String tossWinner = req.getParameter("tossWinner");
		String chooseTo = req.getParameter("chooseTo");
		String [] teamOnePlayingSquad;
		String [] teamTwoPlayingSquad;
		
		teamOnePlayingSquad = req.getParameterValues("teamOnePlayingSquad[]");
		List<String> teamOnePlayingSquadList = Arrays.asList(teamOnePlayingSquad);
		
		teamTwoPlayingSquad = req.getParameterValues("teamTwoPlayingSquad[]");
		List<String> teamTwoPlayingSquadList = Arrays.asList(teamTwoPlayingSquad);
		
		//Create Match
		CreateMatchImpl createMatchObject = new CreateMatchImpl();
		createMatchObject.createMatch(teamOne, teamTwo, rules, overs, tossWinner, chooseTo, teamOnePlayingSquadList, teamTwoPlayingSquadList,resolver);
		
		
		
	}

}
