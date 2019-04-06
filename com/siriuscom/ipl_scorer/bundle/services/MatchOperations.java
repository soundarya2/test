package com.siriuscom.ipl_scorer.bundle.services;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.sling.api.resource.ResourceResolver;

public interface MatchOperations {
	public void inializeMatch(ResourceResolver resolver) throws RepositoryException ;
	public void updateCurrentBatsman(ResourceResolver resolver, String batsmanId, String currentMatchPath, String currentBattingTeamPath)throws UnsupportedRepositoryOperationException, RepositoryException;
	public void updateCurrentBowler(ResourceResolver resolver, String bowlerId, String currentMatchPath, String currentBowlingTeamPath)throws UnsupportedRepositoryOperationException, RepositoryException;
	public void updateScore(ResourceResolver resolver, String currentMatchPath,String currentInnings, String currentBowlerPath , String currentBatsmanPath , String currentBattingTeamPath, String currentBowlingTeamPath, Integer currentBall, Integer currentOver, Integer currentBallRun, String currentBallRunType, boolean isWicket, boolean isBallValid, Integer firstInningsTotal,Integer firstInningsWickets, Integer secondInningsTotal, Integer secondInningsWickets, Integer extras, String [] thisOver,Integer runsToBeUpdated) throws UnsupportedRepositoryOperationException, RepositoryException ;
}
