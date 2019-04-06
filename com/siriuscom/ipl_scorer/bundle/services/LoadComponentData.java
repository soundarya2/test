package com.siriuscom.ipl_scorer.bundle.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.siriuscom.ipl_scorer.bundle.dataproviders.BreadCrumbComponent;
import com.siriuscom.ipl_scorer.bundle.dataproviders.NavBarComponent.Tree;
import com.siriuscom.ipl_scorer.bundle.dataproviders.SampleOne;

@Component(service = SampleOne.class)
public class LoadComponentData implements SampleOne {


	@Reference
	ResourceResolverFactory resourceResolverFactory;

	ResourceResolver resourceResolver;

	public BreadCrumbComponent loadBreadCrumbComponentData(String root) throws LoginException {
		System.out.println("Inside Breadcrumb Component");

			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
		
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

			List<String> rootDir = new ArrayList<String>();
			List<String> pageLinks = new ArrayList<String>();

			String splitRootPath[] = root.split("/");
			StringBuilder path = new StringBuilder("/" + splitRootPath[1]);
			int len = splitRootPath.length;
			for (int i = 2; i < len; i++) {
				path.append("/" + splitRootPath[i]);
				Page rootPage = pageManager.getPage(path.toString());
				pageLinks.add(path.toString() + ".html");
				rootDir.add(rootPage.getTitle());

				System.out.println(rootPage.getTitle());
			}
			return new BreadCrumbComponent(rootDir, pageLinks);
	

	
	}

	public Page getPageFromPath(String path) throws LoginException {
		resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = pageManager.getPage(path);
		return page;
	}

	public void recurse(Tree root, Page rootPage) throws LoginException {

		Iterator<Page> rootPageIterator = rootPage.listChildren();
		System.out.println(root.getLevel());
		while (rootPageIterator.hasNext() && root.getLevel() < 2) {

			String path = rootPageIterator.next().getPath();
			Page page = getPageFromPath(path);
			String title = page.getTitle();
			System.out.println("children-->" + title);

			path += ".html";
			List<Tree> lst = new ArrayList<Tree>();
			Tree tree = new Tree(path, title, lst, root.getLevel() + 1);
			root.getList().add(tree);
			recurse(tree, page);
		}

	}

	public Tree loadNavBarComponentData(String rootPagePath) throws LoginException {
		System.out.println("Inside buildNchild Tree");
		System.out.println(rootPagePath);
		Page page = getPageFromPath(rootPagePath);
		List<Tree> list = new ArrayList<Tree>();
		Tree root = new Tree(rootPagePath, page.getTitle(), list, 0);
		recurse(root, page);
		return root;
	}



}
