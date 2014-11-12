
package com.roopasoft.messages;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class ServerInterface 
{
	static InputStream is = null;
	static JSONObject jObj = null;
	static JSONArray jsonArray = null;
	static String json = "";

	public static ServerInterface instance = null;

	public static ServerInterface getInstance()
	{
		if(instance == null)
			instance = new ServerInterface();
		return instance;
	}

	public String executeHttpGetRequest(String Server_URL) 
	{
		String result = "";
		try 
		{
			URL url = new URL(Server_URL);
			URLConnection connection = url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			//connection.setRequestProperty( "Accept-Encoding", "" );
			//connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			DataInputStream dataIn = new DataInputStream(connection.getInputStream()); 
			String inputLine;
			while ((inputLine = dataIn.readLine()) != null) 
			{
				result += inputLine;
			}
			dataIn.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			result = "";
		}
		return result;
	}
	public String executeHttpPOSTRequest(String Server_URL, Map<String , String> data) throws UnsupportedEncodingException
	{
		String result = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8); 
		try 
		{
			URL url = new URL(Server_URL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

			//Send request
			DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
			//BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
			Set key = data.keySet();
			Iterator keyIte = key.iterator();
			String content = "";
			try
			{
				for(int i = 0; keyIte.hasNext(); i++)
				{
					Object key1 = keyIte.next();
					if(i!= 0)
					{
						content += "&"; 
					}
					content += key1+ "=" + URLEncoder.encode(data.get(key1) , "UTF-8");
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				System.out.print(content);
				dataOut.writeBytes(content);
				dataOut.flush();
				dataOut.close();
			}

			//Get Response
			DataInputStream dataIn = new DataInputStream(connection.getInputStream()); 
			String inputLine;
			while ((inputLine = dataIn.readLine()) != null) 
			{
				result += inputLine;
			}
			dataIn.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return result;
	}
	public String executeHttpRequest(String Server_URL, Map<String , String> data)
	{
		String result = "";
		try 
		{
			URL url = new URL(Server_URL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			//Send request
			DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
			Set key = data.keySet();
			Iterator keyIte = key.iterator();
			String content = "";
			try{
				for(int i = 0; keyIte.hasNext(); i++){
					Object key1 = keyIte.next();
					if(i!= 0){
						content += "&"; 
					}
					content += key1+ "=" + URLEncoder.encode(data.get(key1) , "UTF-8");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			System.out.print(content);
			dataOut.writeBytes(content);
			dataOut.flush();
			dataOut.close();

			//Get Response
			DataInputStream dataIn = new DataInputStream(connection.getInputStream()); 
			String inputLine;
			while ((inputLine = dataIn.readLine()) != null) 
			{
				result += inputLine;
			}
			dataIn.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			result = "";
		}
		return result;
	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) 
	{
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();
		} 
		catch (Exception e) 
		{
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try 
		{
			jObj = new JSONObject(json);            
		}
		catch (JSONException e)
		{
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}	 
		// return JSON String
		return jObj;	 
	}

	public JSONArray getJSONDataFromUrl(String url, List<NameValuePair> params) 
	{
		try 
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line);
			}
			is.close();
			json = sb.toString();
		} 
		catch (Exception e) 
		{
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		try 
		{
			jsonArray = new JSONArray(json);
		}
		catch (JSONException e)
		{
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}	 
		
		return jsonArray;	 
	}
}
