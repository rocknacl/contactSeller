package web_brokers;

import java.util.HashMap;
import java.util.Map;

import helpers.HttpRequestGenerator;
import models.Operation;
import sys.SendEmailSystem;

public class AsinRankingWebBroker {
	static final String url = "http://"+SendEmailSystem.host+":8080/AmazonWebTools/AsinRankingServlet";
	public static String getSEOASIN() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.GetSEOAsin.toString());
		String result = HttpRequestGenerator.net(url, params, "POST");
		return result;
	}
	
	public static String saveRankInformation(String asin,String rank) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.SaveAsinRanking.toString());
		params.put("asin", asin);
		params.put("rank", rank);
		String result = HttpRequestGenerator.net(url, params, "POST");
		return result;
	}
	

}
