package com.example.myapplication.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Http Client tools
 */
public class HttpUtil {

	
	/**
	 * send post request
	 * 
	 * @param url
	 * @param header
	 * @param body
	 * @return
	 */
	public static String doPost1(String url, Map<String, String> header, byte[] body) {
		String result = "";
		BufferedReader in = null;
		try {
			// set url
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
			// set header
			for (String key : header.keySet()) {
				httpURLConnection.setRequestProperty(key, header.get(key));
			}
			// set request body
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestProperty("Content-Type", "binary/octet-stream");
						
			OutputStream out = httpURLConnection.getOutputStream();
			out.write(body);
			out.flush();
			out.close();
			if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
				System.out.println("Http request failed with status code:" + httpURLConnection.getResponseCode());
				return null;
			}

			// Get the response body
			in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	
}
