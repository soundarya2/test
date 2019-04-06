package com.siriuscom.ipl_scorer.bundle.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpClient {
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
