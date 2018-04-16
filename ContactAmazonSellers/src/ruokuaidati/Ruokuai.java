package ruokuaidati;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.remote.JsonException;

public class Ruokuai {
	static CloseableHttpClient client = HttpClients.createDefault();
	static private String username = "rocknacl";
	static private String password = "sy19930320";
	static private int typeid = 3060;
	static String url = "http://api.ruokuai.com/create.json";
	static String reportErrorUrl = "http://api.ruokuai.com/reporterror.json";
	public static String submit(String imgurl) {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("typeid", typeid + ""));
		params.add(new BasicNameValuePair("imageurl", imgurl));
		params.add(new BasicNameValuePair("softid", 1 + ""));
		params.add(new BasicNameValuePair("softkey", "b40ffbee5c1cf4e38028c197eb2fc751"));

		HttpEntity entity = EntityBuilder.create().setParameters(params)
				.setContentType(ContentType.APPLICATION_FORM_URLENCODED).build();
		post.setEntity(entity);
		CloseableHttpResponse response;
		try {
			response = client.execute(post);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String reportMistake(String problemId) {
		HttpPost post = new HttpPost(reportErrorUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("id", problemId));
		params.add(new BasicNameValuePair("softid", 1 + ""));
		params.add(new BasicNameValuePair("softkey", "b40ffbee5c1cf4e38028c197eb2fc751"));

		HttpEntity entity = EntityBuilder.create().setParameters(params)
				.setContentType(ContentType.APPLICATION_FORM_URLENCODED).build();
		post.setEntity(entity);
		CloseableHttpResponse response;
		try {
			response = client.execute(post);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DaAn deal(String imgurl) {
		try {
			String json = submit(imgurl);
			System.out.println(json);
			JSONObject obj = new JSONObject(json);
			String result = obj.getString("Result");
			String id = obj.getString("Id");
			DaAn da = new DaAn(id, result);
			return da;
		} catch (JsonException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		DaAn da = Ruokuai.deal("https://images-na.ssl-images-amazon.com/captcha/uyvnnjxx/Captcha_jsokxkyzsk.jpg");
		System.out.println(da.getAnswer());
		System.out.println(da.getId());
	}
}
