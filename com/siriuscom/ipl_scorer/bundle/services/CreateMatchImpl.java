package com.siriuscom.ipl_scorer.bundle.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

@Component(immediate=true,service =CreateMatch.class,name="Create Match Service")
public class CreateMatchImpl implements CreateMatch {
	

	@Override
	public void createMatch(String teamOne, String teamTwo, String rules, String overs, String tossWinner, String chooseTo,
			List<String> teamOnePlayingSquadList, List<String> teamTwoPlayingSquadList,ResourceResolver resolver) {
		
		//Date for match Name
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss_a");
		String strDate= formatter.format(date);
		String matchName = "match_"+strDate;
		
		//Creating Match
		Session session = resolver.adaptTo(Session.class);		
		Workspace workspace = session.getWorkspace();		
        Resource resource = resolver.getResource("/content/ipl-scorer/schema/match_schema"); 
        Node matchSchema = resource.adaptTo(Node.class);
        try {
        	Node matchNode = matchSchema.addNode(matchName);
        	
        	//match essentials
        	matchNode.setProperty("is_current_match", true);
        	int totalOvers = Integer.parseInt(overs);
        	matchNode.setProperty("overs", totalOvers);
        	     	
        	//Toss Results
        	Node tossResults = matchNode.addNode("toss_results");
        	tossResults.setProperty("chose_to", chooseTo);
        	tossResults.setProperty("winner", tossWinner);
        	
        	//Match Results
        	matchNode.addNode("match_results");
        	
        	//Rules Followed
        	matchNode.addNode("rules_followed");
        	session.save();        	
        	workspace.copy("/content/ipl-scorer/schema/rules_schema/"+rules, "/content/ipl-scorer/schema/match_schema/"+matchName+"/rules_followed/"+rules);
        	
        	//Playing teams
        	Node playingTeams = matchNode.addNode("playing_teams");
        	Node teamOneNode = playingTeams.addNode(teamOne);
        	Node teamTwoNode = playingTeams.addNode(teamTwo);
        	session.save();
        	createPlayingTeam(teamOneNode,teamOnePlayingSquadList,session,teamOne,matchName,overs);
        	createPlayingTeam(teamTwoNode,teamTwoPlayingSquadList,session,teamTwo,matchName,overs);

        	//innings details
        	matchNode.setProperty("current_innings", "first");
        	if(tossWinner == teamOne) {
        		if(chooseTo == "Batting") {
        			setInningsDetails(matchNode, teamOneNode, teamTwoNode);
        			session.save();
        		}
        		else {
        			
        			setInningsDetails(matchNode, teamTwoNode, teamOneNode);
        			session.save();
        		}
        	}else {
        		if(chooseTo == "Batting") {
        			setInningsDetails(matchNode, teamTwoNode, teamOneNode);
        			session.save();
        		}
        		else {
        			setInningsDetails(matchNode, teamOneNode, teamTwoNode);
        			session.save();
        		}      		
        	}       	      	
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
				
	}
	
	
	//creates playing teams subtree node inside match node
	public void createPlayingTeam(Node team,List<String> playingSquad,Session session,String teamName,String matchName,String overs) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		Workspace workspace = session.getWorkspace();
		team.addNode("playing_squad");
		team.addNode("batting_stats");
		team.addNode("bowling_stats");
		session.save();
		
		//playing squad
		for(String employeeId : playingSquad) {
			workspace.copy("/content/ipl-scorer/schema/employee_schema/"+employeeId, "/content/ipl-scorer/schema/match_schema/"+matchName+"/playing_teams/"+teamName+"/playing_squad/"+employeeId);
			session.save();
		}
		
		//overs node
		Integer oversCount = Integer.parseInt(overs);
		Node oversNode = team.addNode("overs");
		for(int i = 1 ; i<=oversCount ; i++) {
			oversNode.addNode("over_"+i);
			session.save();
		}
		
	}
	
	//sets innings details
	public void setInningsDetails(Node matchNode,Node firstBattingNode,Node secondBattingNode) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		//first innings
		matchNode.setProperty("first_innings_batting_team_path", firstBattingNode.getPath());
		matchNode.setProperty("first_innings_bowling_team_path", secondBattingNode.getPath());
	    //second innings
		matchNode.setProperty("second_innings_batting_team_path", secondBattingNode.getPath());
		matchNode.setProperty("second_innings_bowling_team_path", firstBattingNode.getPath());
	}
	
}
