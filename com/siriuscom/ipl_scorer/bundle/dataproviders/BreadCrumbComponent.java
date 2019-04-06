package com.siriuscom.ipl_scorer.bundle.dataproviders;

import java.util.List;

import org.apache.sling.api.resource.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class BreadCrumbComponent extends WCMUsePojo {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	List<String> rootDirNames;
	List<String> pageLinks;

	public BreadCrumbComponent() {

	}

	public BreadCrumbComponent(List<String> rootDir, List<String> pageLinks) {
		this.rootDirNames = rootDir;
		this.pageLinks = pageLinks;
	}

	public List<String> getRootDirNames() {
		return rootDirNames;
	}

	public void setRootDirNames(List<String> rootDirNames) {
		this.rootDirNames = rootDirNames;
	}

	public List<String> getPageLinks() {
		return pageLinks;
	}

	public void setPageLinks(List<String> pageLinks) {
		this.pageLinks = pageLinks;
	}

	@Override
	public void activate() throws Exception {
		getAbsolutePath();

	}

	public void getAbsolutePath() throws LoginException {
		logger.info("getAbsolutePath");
		System.out.println(getCurrentPage().getPath());
		SampleOne sampleOne = getSlingScriptHelper().getService(SampleOne.class);
		BreadCrumbComponent breadCrumbCoponent = sampleOne.loadBreadCrumbComponentData(getCurrentPage().getPath());
		setRootDirNames(breadCrumbCoponent.getRootDirNames());
		setPageLinks(breadCrumbCoponent.getPageLinks());

	}
}
