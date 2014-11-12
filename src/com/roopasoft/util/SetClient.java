package com.roopasoft.util;

import java.util.Properties;
import com.oDesk.api.Config;
import com.oDesk.api.OAuthClient;

public class SetClient 
{
	public OAuthClient client;
	Properties props;
	Config config;
	
	public OAuthClient getclient()
	{
		client = null;
		props = new Properties();
		props.setProperty("consumerKey", com.roopasoft.util.Config.ODESK_CONSUMER_KEY);
		props.setProperty("consumerSecret", com.roopasoft.util.Config.ODESK_CONSUMER_SECRET);
		config = new Config(props);
		client = new OAuthClient(config);
		
		return client;
	}
}
