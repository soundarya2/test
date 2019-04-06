package com.siriuscom.ipl_scorer.bundle.dataproviders;

import com.adobe.cq.sightly.WCMUsePojo;

public class TestClass extends WCMUsePojo {
	private String testString = "Hello IPL Scorer";
	@Override
	public void activate() throws Exception {
		// TODO Auto-generated method stub
	}
	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	
	
}
