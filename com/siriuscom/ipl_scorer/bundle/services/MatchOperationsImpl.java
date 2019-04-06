package com.siriuscom.ipl_scorer.bundle.services;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;



@Component(immediate=true,service =MatchOperations.class,name="Match Operations")
public class MatchOperationsImpl implements MatchOperations {

	@Override
	public void inializeMatch(ResourceResolver resolver) throws RepositoryException {
		Session session = resolver.adaptTo(Session.class);
		Workspace workspace = session.getWorkspace();
		Resource matchResource = resolver.getResource("/content/ipl-scorer/schema/match_schema");
		Node matchSchemaNode = matchResource.adaptTo(Node.class);
		if(matchSchemaNode.hasNode("current_match_version")) {
			return;
		}else {
			
			Node currentMatchNode = getCurrentMatch(resolver);
			String [] value = {};
			String [] batsmenIds = {};
			currentMatchNode.addMixin("mix:versionable");
			currentMatchNode.setProperty("current_batsman", "-");
			currentMatchNode.setProperty("current_bowler", "-");
			currentMatchNode.setProperty("current_over", 0);
			currentMatchNode.setProperty("current_ball", 0);
			currentMatchNode.setProperty("total_runs", 0);
			currentMatchNode.setProperty("extras" , 0);
			currentMatchNode.setProperty("balls", value);
			currentMatchNode.setProperty("is_power_play", false);
			currentMatchNode.setProperty("first_innings_total", 0);
			currentMatchNode.setProperty("second_innings_total", 0);
			currentMatchNode.setProperty("first_innings_wickets", 0);
			currentMatchNode.setProperty("second_innings_wickets", 0);
			currentMatchNode.setProperty("first_innings_extras", 0);
			currentMatchNode.setProperty("second_innings_extras", 0);
			currentMatchNode.setProperty("is_match_over" , false);
			currentMatchNode.setProperty("current_over_actual_ball", 0);
			
			Node batsmanOrderNode = currentMatchNode.addNode("batsman_order");
			batsmanOrderNode.setProperty("batsmenIds", batsmenIds);
			//TO DO : calculate no.of players in the players list and enter the value here
			batsmanOrderNode.setProperty("batsman_couter", 0);
			
			session.save();
			VersionManager versionManager = workspace.getVersionManager();
			Version version = versionManager.checkin(currentMatchNode.getPath());
			
			Node currentMatchVersion = matchSchemaNode.addNode("current_match_version");
			currentMatchVersion.setProperty("current_version", version.getName());
			session.save();
		}
		
		

	}
	
	
	/**
	 * @param matchSchemaNode
	 * @return
	 * @throws RepositoryException
	 * 
	 * This method returns the currentMatch
	 */
	public Node getCurrentMatch(ResourceResolver resolver) throws RepositoryException {
		Resource matchResource = resolver.getResource("/content/ipl-scorer/schema/match_schema");
		Node matchSchemaNode = matchResource.adaptTo(Node.class);
		NodeIterator nodeIterator = matchSchemaNode.getNodes();
		while(nodeIterator.hasNext()) {
			Node node = nodeIterator.nextNode();
			if(node.hasProperty("is_current_match")) {
				Property currentMatchProperty = node.getProperty("is_current_match");
				if(currentMatchProperty.getBoolean()) {
					return node;
				}
			}
		}
		return null;
		
	}


	@Override
	public void updateCurrentBatsman(ResourceResolver resolver, String batsmanId, String currentMatchPath,
			String currentBattingTeamPath) throws UnsupportedRepositoryOperationException, RepositoryException {
		Session session = resolver.adaptTo(Session.class);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		
		//current match 
		Resource currentMatchResource = resolver.getResource(currentMatchPath);
		Node currentMatchNode = currentMatchResource.adaptTo(Node.class);
		versionManager.checkout(currentMatchNode.getPath());
		
		//current batting team
		Resource battingTeamBattingStatsResource = resolver.getResource(currentBattingTeamPath+"/batting_stats");
		Node battingTeamBattingStatsNode = battingTeamBattingStatsResource.adaptTo(Node.class);
		
		if(battingTeamBattingStatsNode.hasNode(batsmanId) == false) {
			workspace.copy("/content/ipl-scorer/schema/employee_schema/"+batsmanId, currentBattingTeamPath+"/batting_stats/"+batsmanId);
			session.save();
			
			//current batsman node
			Resource currentBatsmanResource = resolver.getResource(currentBattingTeamPath+"/batting_stats/"+batsmanId);
			Node currentBatsmanNode = currentBatsmanResource.adaptTo(Node.class);
			
			//current batsman batting status
			Node playerBattingStatus = currentBatsmanNode.addNode("player_batting_status");
			playerBattingStatus.setProperty("runs", 0);
			playerBattingStatus.setProperty("balls", 0);
			playerBattingStatus.setProperty("ones", 0);
			playerBattingStatus.setProperty("twos", 0);
			playerBattingStatus.setProperty("threes", 0);
			playerBattingStatus.setProperty("fours", 0);
			playerBattingStatus.setProperty("sixes", 0);
			playerBattingStatus.setProperty("current_over_balls", 0);
			playerBattingStatus.setProperty("no_of_outs", 0);
			
			//updating current batsman details in matchNode
			Property currentBatsmanPath = currentMatchNode.getProperty("current_batsman");
			currentBatsmanPath.setValue(currentBatsmanNode.getPath());
			Property currentBatsmanName = currentBatsmanNode.getProperty("employee_name");
			currentMatchNode.setProperty("current_batsman_name", currentBatsmanName.getString());
			session.save();
		}
		else {
			//current batsman node
			Resource currentBatsmanResource = resolver.getResource(currentBattingTeamPath+"/batting_stats/"+batsmanId);
			Node currentBatsmanNode = currentBatsmanResource.adaptTo(Node.class);
			
			//updating current batsman details in matchNode
			Property currentBatsmanPath = currentMatchNode.getProperty("current_batsman");
			currentBatsmanPath.setValue(currentBatsmanNode.getPath());
			Property currentBatsmanName = currentBatsmanNode.getProperty("employee_name");
			currentMatchNode.setProperty("current_batsman_name", currentBatsmanName.getString());
			session.save();
		}
		
		Version version = versionManager.checkin(currentMatchNode.getPath());
		updateVersion(version.getName(), resolver);
		
	}
	
	@Override
	public void updateCurrentBowler(ResourceResolver resolver, String bowlerId, String currentMatchPath,
			String currentBowlingTeamPath) throws UnsupportedRepositoryOperationException, RepositoryException {
		
		Session session = resolver.adaptTo(Session.class);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		
		//current match 
		Resource currentMatchResource = resolver.getResource(currentMatchPath);
		Node currentMatchNode = currentMatchResource.adaptTo(Node.class);
		versionManager.checkout(currentMatchNode.getPath());
		
		//current bowling team
		Resource bowlingTeamBowlingStatsResource = resolver.getResource(currentBowlingTeamPath+"/bowling_stats");
		Node battingTeamBattingStatsNode = bowlingTeamBowlingStatsResource.adaptTo(Node.class);
		
		if(battingTeamBattingStatsNode.hasNode(bowlerId) == false) {
			workspace.copy("/content/ipl-scorer/schema/employee_schema/"+bowlerId, currentBowlingTeamPath+"/bowling_stats/"+bowlerId);
			session.save();
			
			//current bowler node
			Resource currentBowlerResource = resolver.getResource(currentBowlingTeamPath+"/bowling_stats/"+bowlerId);
			Node currentBowlerNode = currentBowlerResource.adaptTo(Node.class);
			
			//current bowler batting status
			Node playerBowlingStatus = currentBowlerNode.addNode("player_bowling_status");
			playerBowlingStatus.setProperty("overs", 0);
			playerBowlingStatus.setProperty("current_over_balls", 0);
			playerBowlingStatus.setProperty("balls", 0);
			playerBowlingStatus.setProperty("runs", 0);
			playerBowlingStatus.setProperty("wickets", 0);
			playerBowlingStatus.setProperty("economy", 0.0);
			
			//updating current bowler details in matchNode
			Property currentBowlerPath = currentMatchNode.getProperty("current_bowler");
			currentBowlerPath.setValue(currentBowlerNode.getPath());
			Property currentBowlerName = currentBowlerNode.getProperty("employee_name");
			currentMatchNode.setProperty("current_bowler_name", currentBowlerName.getString());
			session.save();

		}
		else {

			//current bowler node
			Resource currentBowlerResource = resolver.getResource(currentBowlingTeamPath+"/bowling_stats/"+bowlerId);
			Node currentBowlerNode = currentBowlerResource.adaptTo(Node.class);		
			
			//updating current bowler details in matchNode
			Property currentBowlerPath = currentMatchNode.getProperty("current_bowler");
			currentBowlerPath.setValue(currentBowlerNode.getPath());
			Property currentBowlerName = currentBowlerNode.getProperty("employee_name");
			currentMatchNode.setProperty("current_bowler_name", currentBowlerName.getString());
			session.save();

		}
		
		Version version = versionManager.checkin(currentMatchNode.getPath());
		updateVersion(version.getName(), resolver);
		
		
	}
	
	public void updateVersion(String versionName , ResourceResolver resolver) throws PathNotFoundException, RepositoryException {
		Session session = resolver.adaptTo(Session.class);
		Resource matchResource = resolver.getResource("/content/ipl-scorer/schema/match_schema/current_match_version");
		Node currentMatchVersionNode = matchResource.adaptTo(Node.class);
		Property currentVersion = currentMatchVersionNode.getProperty("current_version");
		currentVersion.setValue(versionName);
		session.save();
	}


	@Override
	public void updateScore(ResourceResolver resolver, String currentMatchPath, String currentInnings, String currentBowlerPath,
			String currentBatsmanPath, String currentBattingTeamPath, String currentBowlingTeamPath,
			Integer currentBall, Integer currentOver, Integer currentBallRun, String currentBallRunType,
			boolean isWicket, boolean isBallValid, Integer firstInningsTotal, Integer firstInningsWickets,
			Integer secondInningsTotal, Integer secondInningsWickets, Integer extras, String[] thisOver,Integer runsToBeUpdated) throws UnsupportedRepositoryOperationException, RepositoryException {
		
		Session session = resolver.adaptTo(Session.class);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		
		//current match
		Resource matchResource = resolver.getResource(currentMatchPath);
		Node matchNode = matchResource.adaptTo(Node.class);
		versionManager.checkout(matchNode.getPath());
		
		//totalScore,wickets,
		if(currentInnings.equals("first")) {
			matchNode.setProperty("first_innings_total", firstInningsTotal);
			matchNode.setProperty("first_innings_wickets", firstInningsWickets);
			matchNode.setProperty("first_innings_extras", 0);
		}
		else {
			matchNode.setProperty("second_innings_total", secondInningsTotal);
			matchNode.setProperty("second_innings_wickets", secondInningsWickets);
			matchNode.setProperty("second_innings_extras", 0);
		}
		
		//currentOver and ball
		matchNode.setProperty("current_over",currentOver);
		matchNode.setProperty("current_ball", currentBall);
		
		//batsman details
		Resource currentBatsmanResource =  resolver.getResource(currentBatsmanPath);
		Node currentBatsmanNode = currentBatsmanResource.adaptTo(Node.class);
		Node playerBattingStatus = currentBatsmanNode.getNode("player_batting_status");
		Property batsmanRunsProperty = playerBattingStatus.getProperty("runs");
		batsmanRunsProperty.setValue(batsmanRunsProperty.getLong() + currentBallRun);
		
		if(isBallValid) {
			Property batsmanBallsProp = playerBattingStatus.getProperty("balls");
			Long batsmanBallsValue = batsmanBallsProp.getLong() + 1;
			batsmanBallsProp.setValue(batsmanBallsValue);
			Property batsmanCurrentOverBallsProp = playerBattingStatus.getProperty("current_over_balls");
			Long batsmanCurrentOverBallsValue = batsmanCurrentOverBallsProp.getLong() + 1;
			if(batsmanCurrentOverBallsValue == 6) {
				matchNode.setProperty("current_batsman","-");
				matchNode.setProperty("current_batsman_name","-");
				batsmanCurrentOverBallsProp.setValue(0);
			}else {
				batsmanCurrentOverBallsProp.setValue(batsmanCurrentOverBallsValue);
			}
			
			if(isWicket) {
				Property noOfOutsProp = playerBattingStatus.getProperty("no_of_outs");
				noOfOutsProp.setValue(noOfOutsProp.getLong() + 1);
				matchNode.setProperty("current_batsman","-");
				matchNode.setProperty("current_batsman_name","-");
				batsmanCurrentOverBallsProp.setValue(0);
			}
			
		}
		
		
		
		
		//bowler details
		Resource currentBowlerResource =  resolver.getResource(currentBowlerPath);
		Node currentBowlerNode = currentBowlerResource.adaptTo(Node.class);
		Node playerBowlingStatus = currentBowlerNode.getNode("player_bowling_status");
		Property bowlerRunsProperty = playerBowlingStatus.getProperty("runs");
		
		if(isBallValid) {
			bowlerRunsProperty.setValue(bowlerRunsProperty.getLong() + currentBallRun);
			Property bowlerCurrentOverBallsProp = playerBowlingStatus.getProperty("current_over_balls");
			Long bowlerCurrentOverBallsValue = bowlerCurrentOverBallsProp.getLong() + 1;
			if(currentBall == 6) {
				matchNode.setProperty("current_bowler","-");
				matchNode.setProperty("current_bowler_name","-");
				bowlerCurrentOverBallsProp.setValue(0);
			}else {
				bowlerCurrentOverBallsProp.setValue(bowlerCurrentOverBallsValue);
			}
		}else {
			if(!currentBallRunType.equals("p")) {
				bowlerRunsProperty.setValue(bowlerRunsProperty.getLong() + runsToBeUpdated);
			}
			
		}
		
		if(isWicket) {
			Property wickets = playerBowlingStatus.getProperty("wickets");
			wickets.setValue(wickets.getLong() + 1);
		}
		
		 
		//innnings data
		int overNum = currentOver + 1;
		Resource currentBattingTeamCurrentOver =  resolver.getResource(currentBattingTeamPath+"/overs/over_"+overNum);
		Node currentBattingTeamCurrentOverNode = currentBattingTeamCurrentOver.adaptTo(Node.class);
		Long currentActuallBall = matchNode.getProperty("current_over_actual_ball").getLong() + 1;
		Node ballNode = currentBattingTeamCurrentOverNode.addNode("ball_"+currentActuallBall);
		ballNode.setProperty("runs", currentBallRun);
		ballNode.setProperty("desc", currentBallRunType);
		if(currentBall == 6) {
			matchNode.setProperty("current_over_actual_ball", 0);
		}else {
			matchNode.setProperty("current_over_actual_ball", currentActuallBall);
		}
		
		
		//this over
		if(currentBall != 6)
			matchNode.setProperty("balls", thisOver);
		else {
			String [] values = {};
			matchNode.setProperty("balls", values);
		}
			
		
		session.save();
		Version version = versionManager.checkin(matchNode.getPath());
		updateVersion(version.getName(),resolver);
		
		
		
		
		
	}


	


}
