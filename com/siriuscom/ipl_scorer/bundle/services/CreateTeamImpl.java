package com.siriuscom.ipl_scorer.bundle.services;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Workspace;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component(immediate=true,service =CreateTeam.class,name="Create Team Service")
public class CreateTeamImpl implements CreateTeam {
	
	@Override
	public void createTeam(String teamName , List<String> playersList,ResourceResolver resolver) throws LoginException {
		
        Session session = resolver.adaptTo(Session.class);
        Resource nodePathResource = resolver.getResource("/content/ipl-scorer/schema/team_schema");
        Workspace currentWorkspace = session.getWorkspace();
        Node teamSchemaNode=nodePathResource.adaptTo(Node.class);
        
        try {
        	teamName = teamName.replace(" ","_");
        	Node teamNode = teamSchemaNode.addNode(teamName);
        	teamNode.addNode("players_list");
        	session.save();        	
        	for(String employeeId : playersList) {
        		currentWorkspace.copy("/content/ipl-scorer/schema/employee_schema/"+employeeId,"/content/ipl-scorer/schema/team_schema/"+teamName+"/players_list/"+employeeId);
        		session.save();      		
        	}
        	
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
     
	}

}
