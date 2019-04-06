package com.siriuscom.ipl_scorer.bundle.services;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;

public interface CreateMatch {
	/**
	 * @param teamOne
	 * @param teamTwo
	 * @param rules
	 * @param tossWinner
	 * @param chooseTo
	 * @param teamOnePlayingSquadList
	 * @param teamTwoPlayingSquad
	 * 
	 * This method creates a Match.
	 */
	public void createMatch(String teamOne,String teamTwo,String rules, String overs,String tossWinner,String chooseTo,List<String> teamOnePlayingSquadList, List<String> teamTwoPlayingSquadList,ResourceResolver resolver );
}
