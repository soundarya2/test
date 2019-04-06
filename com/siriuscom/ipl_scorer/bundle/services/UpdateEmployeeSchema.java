package com.siriuscom.ipl_scorer.bundle.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TreeMap;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Designate(ocd = UpdateEmployeeSchema.Config.class)
public class UpdateEmployeeSchema {

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	ResourceResolver resourceResolver;
	public static final String EMAIL = "email_id";
	public static final String EMPLOYEE_ID = "employee_id";
	public static final String EMPLOYEE_NAME = "employee_name";
	public static final String EMPLOYEE_PHOTO="avatar";

	@ObjectClassDefinition(name = "Update Employee Schema  Configuration", description = "This is sample configuration")
	public @interface Config {

		// Checkbox
		@AttributeDefinition(name = "Update Enmployee Schema?")
		boolean blogIsActive() default false;

	}

	@Activate
	protected void activate(final Config config) throws AccessDeniedException, ItemExistsException,
			ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException,
			LockException, NoSuchNodeTypeException, LoginException, RepositoryException, JSONException {
		String url = "http://54.175.213.131:3000/api/employee/all";

		String path = "/content/ipl-scorer/schema/employee_schema";

		if (config.blogIsActive()) {

			String json = hitExternalAPI(url);

			updateEmployeeDetails(json, path);
		}

	}

	public TreeMap<String, Integer> sortEmployeeDetails(JSONArray jsonArray) throws JSONException {
		TreeMap<String,Integer> treeMap=new TreeMap<String,Integer>();
        for(int i=0;i<jsonArray.length();i++) {
        	treeMap.put(jsonArray.getJSONObject(i).get("name").toString(),i);
        }
		return treeMap;

	}

	public void updateEmployeeDetails(String json, String path) throws LoginException, AccessDeniedException,
			ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException, RepositoryException, JSONException {

		JSONObject jsonObj = new JSONObject(json);
		JSONArray jsonArray = jsonObj.getJSONArray("data");
		 
	
		
		TreeMap<String,Integer> treeMap = sortEmployeeDetails(jsonArray);
		
		

		
		resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
		Session session = resourceResolver.adaptTo(Session.class);
		Resource nodePathResource = resourceResolver.getResource(path);
		Node feedNode = nodePathResource.adaptTo(Node.class);

		for(String ch:treeMap.keySet()) {
			JSONObject obj = jsonArray.getJSONObject(treeMap.get(ch));

			try {
				Node dataNode = feedNode.addNode(String.valueOf(obj.get("user_id")));
				dataNode.setProperty(EMAIL, obj.get("email").toString());
				dataNode.setProperty(EMPLOYEE_ID, obj.get("user_id").toString());
				dataNode.setProperty(EMPLOYEE_NAME, obj.get("name").toString());
				dataNode.setProperty(EMPLOYEE_PHOTO,obj.get("avatar").toString());
			} catch (Exception e) {
				System.out.println(e.toString());

			}

		}
	
		session.save();
	}

	public String hitExternalAPI(String url) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			final Logger logger = LoggerFactory.getLogger(getClass());
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(response.getStatusLine().getStatusCode());
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String myJSON = "";
			String output;
			while ((output = br.readLine()) != null) {
				myJSON = myJSON + output;
			}
			httpClient.getConnectionManager().shutdown();

			return myJSON;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
