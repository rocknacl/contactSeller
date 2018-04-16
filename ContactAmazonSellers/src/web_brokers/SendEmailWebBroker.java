package web_brokers;

import java.util.HashMap;
import java.util.Map;

import contact_seller_plan2.RobotAccountForRegister;
import helpers.HttpRequestGenerator;
import models.Operation;
import sys.SendEmailSystem;

public class SendEmailWebBroker {
	static String url = "http://" + SendEmailSystem.host + ":8080/AmazonWebTools/SendEmailServlet";

	public static String getSellerID(String country, String taskName) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.GetAmazonSellerID.toString());
		params.put("Country", country);
		params.put("TaskName", taskName);
		String result = HttpRequestGenerator.net(url, params, "POST");
		System.out.println(result);
		return result;
	}

	public static String[] getAmazonAccount(String country, String taskName) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.GetAmazonAccount.toString());
		String result = HttpRequestGenerator.net(url, params, "POST");
		System.out.println(result);
		return result.split(" ");
	}

	public static String saveSendEmailResult(String email, String sellerID, String taskname) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.SaveSendEmailResult.toString());
		params.put("AccountEmail", email);
		params.put("SellerID", sellerID);
		params.put("TaskName", taskname);
		String result = HttpRequestGenerator.net(url, params, "POST");
		System.out.println("saveSendEmailResult : " + result);
		return result;
	}

	public static String saveAccountBlockTime(String email) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.SaveAccountBlockTime.toString());
		params.put("AccountEmail", email);

		String result = HttpRequestGenerator.net(url, params, "POST");
		System.out.println("saveAccountBlockTime : " + result);
		return result;
	}

	public static String saveAccountRegistered(RobotAccountForRegister account) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("OP", Operation.SaveAccountRegistered.toString());
		params.put("email", account.getEmail());
		params.put("password", account.getPassword());
		params.put("name", account.getName());
		params.put("nation", account.getCountry().toString());
		String result = HttpRequestGenerator.net(url, params, "POST");
		System.out.println(result);
		return result;
	}

}
