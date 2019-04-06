package com.siriuscom.ipl_scorer.bundle.services;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MatchSchemaDataUpdate {
	private Logger log=LoggerFactory.getLogger(getClass());
	public void updateMatchSchemaDetails(ResourceResolver resource,String payload)
	{
		try
		{
		CloseableHttpClient client = HttpClients.createDefault();
		Session session=resource.adaptTo(Session.class);
		String match_node=payload.substring(40,47);
	
		Node node=session.getNode("/content/ipl-scorer/schema/match_schema/"+match_node);
		StringWriter stringWriter = new StringWriter();
		JsonItemWriter jsonWriter = new JsonItemWriter(null);
		jsonWriter.dump(node, stringWriter,6, true);
		String matchData = stringWriter.toString();
		System.out.println(matchData);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int yearData = calendar.get(Calendar.YEAR);
		String year=String.valueOf(yearData);
	
		HttpPatch httpPatch=new HttpPatch("https://iplscorer-6ba55.firebaseio.com/"+year+"/"+match_node+".json");
	    
		StringEntity entity = new StringEntity(matchData);
	    httpPatch.setEntity(entity);
	    httpPatch.setHeader("Accept","application/json");
	    httpPatch.setHeader("Content-type", "application/json");
	  
	    
	    System.out.println("HTTP_________"+httpPatch);
	    HttpResponse response2 = client.execute(httpPatch);
	    String out = response2.getEntity().toString();
	    System.out.println(out);
	    client.close();
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}
		
	
	}

