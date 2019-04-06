package com.siriuscom.ipl_scorer.bundle.services;

import java.util.List;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface CreateTeam {
	public void createTeam(String teamName , List<String> playersList,ResourceResolver resourceResolver) throws LoginException;
}
