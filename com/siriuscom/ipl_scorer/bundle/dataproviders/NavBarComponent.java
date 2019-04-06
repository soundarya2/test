package com.siriuscom.ipl_scorer.bundle.dataproviders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;




public class NavBarComponent extends WCMUsePojo {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	Tree root;
    public static  class Tree{
    	    String path;
    		String pageTitle;
    	    List<Tree> list=new ArrayList<Tree>();
    	    int level;
    	    
    		public Tree(String path, String pageTitle,List<Tree> list,int level) {
    			this.path = path;
    			this.pageTitle = pageTitle;
    			this.list=list;
    			this.level=level;
    		}
    		public int getLevel() {
				return level;
			}
			public String getPath() {
    			return path;
    		}
    		public void setPath(String path) {
    			this.path = path;
    		}
    		public String getPageTitle() {
    			return pageTitle;
    		}
    		public void setPageTitle(String pageTitle) {
    			this.pageTitle = pageTitle;
    		}
    		public List<Tree> getList() {
    			return list;
    		}
    		public void setList(List<Tree> list) {
    			this.list = list;
    		}
    }
	public void display(Tree root) {
		System.out.println(root.pageTitle);
		System.out.println(root.level);
		for(Tree tree:root.list) {
			display(tree);
		}
	}

	@Override
	public void activate() throws Exception {
		SampleOne sampleOne = getSlingScriptHelper().getService(SampleOne.class);
		root=sampleOne.loadNavBarComponentData((get("rootPage", String.class)));
		setRoot(root);
		display(root);
	}

	public Tree getRoot() {
		return root;
	}

	public void setRoot(Tree root) {
		this.root = root;
	}

}
