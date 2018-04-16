package web_brokers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import helpers.HttpRequestGenerator;
import models.SellerFeedbackInformation;
import sys.SendEmailSystem;

public class SellerFeedbackWebBroker {
	static String url = "http://" + SendEmailSystem.host + ":8080/AmazonWebTools/SellerFeedbackServlet";

	public static List<String> getSellerIDs(String country) {
		String result;
		List<String> ids = new ArrayList<String>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("country", country);
			result = HttpRequestGenerator.net(url, params, "GET");
			JSONArray jarray = new JSONArray(result);

			for (int i = 0; i < jarray.length(); i++) {
				ids.add(jarray.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static void saveFeedbackInformation(List<SellerFeedbackInformation> infos) {
		JSONArray jarray = new JSONArray();
		for (SellerFeedbackInformation info : infos) {
			JSONObject obj = new JSONObject(info);
			jarray.put(obj);
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("sellerFeedbackInformation", jarray.toString());
		try {

			HttpRequestGenerator.net(url, params, "POST");
			System.out.println("uploaded");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
