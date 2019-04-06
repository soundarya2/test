package com.siriuscom.ipl_scorer.bundle.servlets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component(service = { Servlet.class }, property = { "sling.servlet.resourceTypes=ipl-scorer/score-board-servlet",
		"sling.servlet.methods=POST", "sling.servlet.methods=GET" })
public class ScoreBoardServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	static final String CURRENT_BATSMEN = "current_batsman";
	static final String CURRENT_BOWLER = "current_bowler";

	static final String CURRENT_BATSMEN_NAME = "current_batsman_name";
	static final String CURRENT_BOWLER_NAME = "current_bowler_name";

	static final String CURRENT_OVER = "current_over";
	static final String OVERS = "overs";
	static final String BALLS = "balls";
	static final String CURRENT_MATCH = "is_current_match";
	static final String INNINGS = "current_innings";

	static final String FIRST_INNINGS_RUNS = "first_innings_total";
	static final String SECOND_INNINGS_RUNS = "second_innings_total";
	static final String FIRST_INNINGS_WICKETS = "first_innings_wickets";
	static final String SECOND_INNINGS_WICKETS = "second_innings_wickets";
	static final String MATCH_OVER = "is_match_over";
	static final String NOTIFICATIONS = "notifications";
	static final String TOSS_RESULTS = "toss_results";

	static final String FIRST_INNINGS_BATTING_TEAM_PATH = "first_innings_batting_team_path";

	static Map<String, String> mapResults = new HashMap<>();

	static Map<String, String> questions = new HashMap<>();
	static Set<String> commons = new HashSet<>();
	static int ball_cnt = 0;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("IN DO GET ::");
		System.out.println("testGetData :: " + req.getParameter("testGetData"));

	}

	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		Instant start = Instant.now();

		try {

			activate();
		} catch (JSONException e) {
			System.out.println(e);
		}

		resp.getWriter().print(new JSONObject(mapResults));
		Instant end = Instant.now();

		Duration interval = Duration.between(start, end);

		System.out.println("Execution time in seconds: " + interval.getSeconds());

	}

	public static class Tree {

		public String nodeName;
		public String value;
		public List<Tree> list;
		String parent;

		Tree(String nodeName, List<Tree> list, String value, String parent) {
			this.nodeName = nodeName;
			this.list = list;
			this.value = value;
			this.parent = parent;
		}
	}

	public static void searchTree(Tree root) {
		String temp = root.parent;

		if (root.parent.contains(mapResults.get(INNINGS) + "_innings_batting_team_path")) {
			mapResults.put("current_batting_team", preProcess(temp, true));
		}
		if (root.parent.contains(mapResults.get(INNINGS) + "_innings_bowling_team_path")) {
			mapResults.put("current_bowling_team", preProcess(temp, true));
		}
		for (Tree tree : root.list) {
			searchTree(tree);
		}

		for (String question : questions.keySet()) {
			String q = questions.get(question);
			if (root.parent.contains(q)) {

				String splitAnswer[] = root.parent.split("/");
				String prevWord = q.split("/")[q.split("/").length - 1];

				for (int j = 0; j < splitAnswer.length; j++) {

					if (splitAnswer[j].equals(prevWord)) {

						if (j + 1 < splitAnswer.length) {
							mapResults.put(question, splitAnswer[j + 1]);
						}

					}
				}
			}

		}

	}

	public static String preProcess(String data, boolean returnLast) {
		String split[] = data.split("/");
		StringBuilder sb = new StringBuilder("root");
		if (!returnLast) {
			boolean found = false;
			for (int i = 0; i < split.length; i++) {
				if (split[i].equals("playing_teams")) {
					found = true;
				}
				if (found) {
					sb.append("/" + split[i]);
				}
			}
			String temp = sb.toString();
			return temp;
		} else {
			return split[split.length - 1];
		}

	}

	public static void parseJson(Tree root, JSONObject object, JSONArray keys, int index) throws JSONException {
		String key = keys.getString(index);

		if (object.get(key) instanceof JSONObject) {

			JSONArray k = object.getJSONObject(key).names();

			List<Tree> list = new ArrayList<Tree>();
			Tree parent = new Tree(key, list, "none", root.parent + key + "/");
			root.list.add(parent);

			if (key.equals(TOSS_RESULTS)) {
				questions.put(key + "_winner", key + "/winner");
				questions.put(key + "_choose_to", key + "/chose_to");
			}

			parseJson(parent, object.getJSONObject(key), k, 0);

		} else if (object.get(key) instanceof JSONArray) {

			JSONArray jsonArray = (JSONArray) object.get(key);

			for (int i = 0; i < jsonArray.length(); i++) {
				if (jsonArray.get(i) instanceof JSONObject) {

					JSONArray k = jsonArray.getJSONObject(i).names();
					parseJson(root, (JSONObject) jsonArray.get(i), k, 0);

				} else {
					System.out.println("leaves inside array-->" + jsonArray.get(i).toString());
					if (key.equals(BALLS)) {
						mapResults.put("ball_" + String.valueOf(ball_cnt), jsonArray.get(i).toString());
						ball_cnt = ball_cnt + 1;
					}

				}
			}

		}

		else {

			if (commons.contains(key)) {
				mapResults.put(key, object.get(key).toString());
			}

			if (key.equals(CURRENT_BATSMEN) || key.equals(CURRENT_BOWLER)) {
				String temp = preProcess(object.get(key).toString(), false);
				questions.put(key + "_avatar", temp + "/avatar");
				questions.put(key + "_employee_name", temp + "/employee_name");
				if (key.equals(CURRENT_BATSMEN)) {
					questions.put(key + "_balls", temp + "/player_batting_status/balls");
					questions.put(key + "_runs", temp + "/player_batting_status/runs");
				}
				if (key.equals(CURRENT_BOWLER)) {
					questions.put(key + "_wickets", temp + "/player_bowling_status/wickets");
					questions.put(key + "_runs", temp + "/player_bowling_status/runs");
				}

			}

			List<Tree> list = new ArrayList<Tree>();
			Tree parent = new Tree(key, list, object.get(key).toString(),
					root.parent + key + "/" + object.get(key).toString());
			root.list.add(parent);
		}

		if (index + 1 < keys.length()) {
			parseJson(root, object, keys, index + 1);
		}
	}

	public static String getLast(String toSplit) {
		return toSplit.split("/")[toSplit.split("/").length - 1];
	}

	public static int calculate_balls(String overs) {
		int total = 0;
		String split_overs[] = overs.split(".");
		total += Integer.valueOf(split_overs[0]) * 6;
		total += Integer.valueOf(split_overs[1]);
		return total;
	}

	public static void logic() {
		String result = "";

		for (String ch : mapResults.keySet()) {
			System.out.println("===================>" + ch);
			if (ch.equals(INNINGS) && mapResults.get(ch).equals("first")) {
				System.out.println("here--->");
				result = (mapResults.get("toss_results_winner") + " won the toss and chose to "
						+ mapResults.get("toss_results_choose_to"));
			} else if (ch.equals(INNINGS) && mapResults.get(ch).equals("second")) {

				int diff_runs = Math.abs(Integer.valueOf(mapResults.get(FIRST_INNINGS_RUNS))
						- Integer.valueOf(mapResults.get(SECOND_INNINGS_RUNS)));

				int diff_overs = (calculate_balls((mapResults.get(OVERS)))
						- (calculate_balls(mapResults.get(CURRENT_OVER))));

				if (Integer.valueOf(mapResults.get(FIRST_INNINGS_RUNS)) < Integer
						.valueOf(mapResults.get(SECOND_INNINGS_RUNS))) {
					result = mapResults.get("current_batting_team") + " leads by " + String.valueOf(diff_runs)
							+ " runs ";

					if (ch.equals(MATCH_OVER) && mapResults.get(ch).equals("true")) {
						result = mapResults.get("current_batting_team") + " won "
								+ getLast(mapResults.get(FIRST_INNINGS_BATTING_TEAM_PATH)) + " by "
								+ String.valueOf(diff_runs) + " runs ";
					}

				} else {
					result = (mapResults.get("current_batting_team") + " needs " + String.valueOf(diff_runs)
							+ " runs from " + String.valueOf(diff_overs)) + " balls ";

					if (ch.equals(MATCH_OVER) && mapResults.get(ch).equals("true")) {
						result = (getLast(mapResults.get(FIRST_INNINGS_BATTING_TEAM_PATH)) + " won "
								+ mapResults.get("current_batting_team") + " by " + String.valueOf(diff_runs)
								+ " runs ");
					}

				}
			}

			mapResults.put(ch, mapResults.get(ch).replace("_", " ").toUpperCase());
			System.out.println(mapResults.get(ch));
		}
		mapResults.put(NOTIFICATIONS, result.toUpperCase());

		System.out.println(mapResults.get(NOTIFICATIONS));

	}

	public static void activate() throws JSONException {

		ball_cnt = 0;
		commons.add(CURRENT_OVER);
		commons.add(OVERS);
		commons.add(INNINGS);
		commons.add(FIRST_INNINGS_RUNS);
		commons.add(SECOND_INNINGS_RUNS);
		commons.add(FIRST_INNINGS_WICKETS);
		commons.add(SECOND_INNINGS_WICKETS);
		commons.add(CURRENT_BATSMEN_NAME);
		commons.add(CURRENT_BOWLER_NAME);
		commons.add(FIRST_INNINGS_BATTING_TEAM_PATH);
		commons.add(MATCH_OVER);

		Instant start = Instant.now();

		String json = hitExternalAPI("https://api.myjson.com/bins/loi90");

		Instant end = Instant.now();

		Duration interval = Duration.between(start, end);

		System.out.println("Execution time in seconds: " + interval.getSeconds());

		JSONObject object = new JSONObject(json);
		JSONArray keys = object.names();

		for (int i = 0; i < keys.length(); i++) {

			if (object.get(keys.get(i).toString()) instanceof JSONObject) {
				JSONObject temp = object.getJSONObject(keys.get(i).toString());

				if (temp.has(CURRENT_MATCH) && temp.get(CURRENT_MATCH).toString().equals("true")) {
					System.out.println("reached");
					callAction(temp);
					break;

				}

			} else if (keys.get(i).toString().equals(CURRENT_MATCH)
					&& object.get(keys.get(i).toString()).toString().equals("true")) {
				callAction(object);
				break;

			}
		}

	}

	public static void callAction(JSONObject temp) throws JSONException {
		List<Tree> list = new ArrayList<Tree>();
		Tree root = new Tree("root", list, "none", "root/");
		JSONArray key = temp.names();
		parseJson(root, temp, key, 0);
		searchTree(root);
		logic();
	}

	public static String hitExternalAPI(String url) {
		try {

			InputStream is = new URL(url).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String myJSON = "";
			String output;
			while ((output = br.readLine()) != null) {
				myJSON = myJSON + output;
			}
			return myJSON;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
