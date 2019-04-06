package com.siriuscom.ipl_scorer.bundle.dataproviders;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.adobe.cq.sightly.WCMUsePojo;
import com.siriuscom.ipl_scorer.bundle.services.HttpClient;


public class MainScoreBoard extends WCMUsePojo {
	
	
	public static class Tree {


		public String nodeName;
		public String value;
		public List<Tree> list;

		Tree(String nodeName, List<Tree> list, int nodeId, String value) {
			this.nodeName = nodeName;
			this.list = list;
			this.value = value;
		}
	}

	@Override
	public void activate() throws Exception {
		System.out.println("reached main");
		HttpClient httpClient=new HttpClient();
		String json=httpClient.hitExternalAPI("https://api.myjson.com/bins/1euc5k");
		JSONObject object = new JSONObject(json);
		JSONArray keys = object.names();
		System.out.println(keys);
		parseJson(object, keys, 0);
		
	}
	
	public void parseJson(JSONObject object, JSONArray keys, int index) throws JSONException {
		String key = keys.getString(index);
		System.out.println("key-->" + key);

		if (object.get(key) instanceof JSONObject) {

			JSONArray k = object.getJSONObject(key).names();
			parseJson(object.getJSONObject(key), k, 0);

		} else if (object.get(key) instanceof JSONArray) {

			JSONArray jsonArray = (JSONArray) object.get(key);

			for (int i = 0; i < jsonArray.length(); i++) {
				if (jsonArray.get(i) instanceof JSONObject) {

					JSONArray k = jsonArray.getJSONObject(i).names();
					parseJson((JSONObject) jsonArray.get(i), k, 0);

				} else {
					System.out.println("leaves inside array-->" + jsonArray.getString(i));
				
				}
			}

		} else {

			System.out.println("leaf-->" + object.getString(key));
		

		}

		if (index + 1 < keys.length()) {
			parseJson(object, keys, index + 1);
		}
	}

}
