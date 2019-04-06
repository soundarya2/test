package com.siriuscom.ipl_scorer.bundle.dataproviders;

import org.apache.sling.api.resource.LoginException;

import com.siriuscom.ipl_scorer.bundle.dataproviders.NavBarComponent.Tree;




public interface SampleOne {
	public BreadCrumbComponent loadBreadCrumbComponentData(String root) throws LoginException;
	public Tree loadNavBarComponentData(String root) throws LoginException;
}
