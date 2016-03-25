package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class UploadToServer {

	private static final String UTF8 = "utf-8";
	//private static final String URL = "http://wishconsole.com/user/batchupload.php";
	private static final String URL = "http://localhost/wishhelper/user/batchupload.php";
	private static UploadToServer server;

	private UploadToServer() {
	}

	public static synchronized UploadToServer getInstance() {
		if (server == null)
			server = new UploadToServer();
		return server;
	}

	public boolean upload(Map<String, String> values) {
		boolean result = false;

		// 使用Post方式，组装参数
		HttpPost httpost = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			nvps.add(new BasicNameValuePair(key, values.get(key)));
		}
		
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		try {

			// 创建httpclient链接，并执行
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(httpost);

			// 对于返回实体进行解析
			HttpEntity entity = response.getEntity();
			InputStream returnStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					returnStream, UTF8));
			StringBuilder resultvalue = new StringBuilder();
			String str = null;
			while ((str = reader.readLine()) != null) {
				resultvalue.append(str).append("\n");
			}

			System.out.println("returned value:" + resultvalue.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
