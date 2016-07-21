package com.meishai.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetUtils {
	private static String TAG = "tag";

	public static boolean hasNet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static int getNetType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null) {
			Log.d(TAG, "wmz:netTypeName=" + info.getTypeName());
			return info.getType();
		}
		return -1;

	}

	public static HttpURLConnection getHttpURLConnection(String url) {
		HttpURLConnection httpurlconnection = null;

		try {
			httpurlconnection = (HttpURLConnection) new URL(url)
					.openConnection();
			httpurlconnection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.106 Safari/535.2");
			return httpurlconnection;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream getInputStreamFromHttpURLConnectionByGet(
			String url) {
		try {

			HttpURLConnection httpurlconnection = null;

			httpurlconnection = getHttpURLConnection(url);
			if(httpurlconnection==null){
				Log.d("tag", "wmz:conn=null");
				return null; 
			}
			httpurlconnection.setDoInput(true);
			httpurlconnection.setRequestMethod("GET");
			httpurlconnection.setReadTimeout(60 * 1000);
			httpurlconnection.setConnectTimeout(60 * 1000);

			return httpurlconnection.getInputStream();

		} catch (MalformedURLException e) {
			Log.d("tag", "wmz:e="+e.toString());
			return null;
		} catch (IOException e) {
			Log.d("tag", "wmz:e="+e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream getInputStreamFromHttpURLConnectionByPost(
			String url, String params) {
		try {

			HttpURLConnection httpurlconnection = null;

			httpurlconnection = getHttpURLConnection(url);
			if(httpurlconnection==null) return null; 
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setDoInput(true);
			httpurlconnection.setRequestMethod("POST");
			httpurlconnection.getOutputStream().write(params.getBytes());
			httpurlconnection.setReadTimeout(60 * 1000);
			httpurlconnection.setConnectTimeout(60 * 1000);

			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			return httpurlconnection.getInputStream();

		} catch (MalformedURLException e) {
			Log.d("tag", "wmz:e="+e.toString());
			return null;
		} catch (IOException e) {
			Log.d("tag", "wmz:e="+e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public static String getStringFromHttpURLConnectionByGet(String url) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = null;
			InputStream input = null;

			input = getInputStreamFromHttpURLConnectionByGet(url);
			if (input == null){
				Log.d("tag", "wmz:input=null");
				return null;
			}
			in = new BufferedReader(new InputStreamReader(input, "utf-8"));
			int len = 0;
			char buf[] = new char[4 * 1024];
			while ((len = in.read(buf)) != -1) {
				sb.append(new String(buf, 0, len));
			}
			in.close();

		} catch (MalformedURLException e) {
			Log.d("tag", "wmz:e="+e.toString());
			return null;
		} catch (IOException e) {
			Log.d("tag", "wmz:e="+e.toString());
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	public static String getStringFromHttpURLConnectionByPost(String url,
															  String params) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = null;
			InputStream input = null;

			input = getInputStreamFromHttpURLConnectionByPost(url, params);
			if (input == null) {
				Log.d("tag", "wmz:null");
				return null;
			}
			in = new BufferedReader(new InputStreamReader(input, "utf-8"));
			int len = 0;
			char buf[] = new char[4 * 1024];
			while ((len = in.read(buf)) != -1) {
				sb.append(new String(buf, 0, len));
			}
			in.close();

		} catch (MalformedURLException e) {
			Log.d("tag", "wmz:e="+e.toString());
			return null;
		} catch (IOException e) {
			Log.d("tag", "wmz:e="+e.toString());
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	
	
}
