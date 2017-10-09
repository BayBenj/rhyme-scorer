package data;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public abstract class HttpInterface {

	public static List<JSONObject> get(String query) throws IOException, JSONException {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(query);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
				System.out.println(query);
				System.out.println(line + "\n");
			}
			return DataLoader.parseJson(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
