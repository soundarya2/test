package com.siriuscom.ipl_scorer.bundle.servlets;

import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.siriuscom.ipl_scorer.bundle.services.CreateTeamImpl;

@Component(service = { Servlet.class },property = {
        "sling.servlet.resourceTypes=ipl-scorer/create-team-servlet",
         "sling.servlet.methods=POST", 
         "sling.servlet.methods=GET"
    })
public class CreateTeamServlet extends SlingAllMethodsServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4878691563770684204L;
	

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException{
		System.out.println("IN DO GET ::");
		System.out.println("testGetData :: " + req.getParameter("testGetData"));
	}
	
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws ServletException {
		String [] selectedPlayers;
		selectedPlayers = req.getParameterValues("players[]");
		List<String> playersList = Arrays.asList(selectedPlayers);
		String teamName = req.getParameter("teamName");
		CreateTeamImpl createTeamService = new CreateTeamImpl();
	    ResourceResolver resolver = req.getResourceResolver();
		
			try {
				createTeamService.createTeam(teamName, playersList,resolver);
			} catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
}
