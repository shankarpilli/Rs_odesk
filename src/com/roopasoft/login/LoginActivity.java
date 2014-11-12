package com.roopasoft.login;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.oDesk.api.OAuthClient;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.MainActivity;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class LoginActivity extends Activity implements OnClickListener
{

	Context context;
	SharedPreferences prefs;
	WebView webview;
	Button get_token,loadurl;
	String authzUrl;
	String authzUrlcallback;
	List<String> verifier;
	OAuthClient client;
	ShowToast toast;
	ConnectionDetector connectionDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		context = this;
		connectionDetector = new ConnectionDetector(context);
		
		toast = new ShowToast(getApplicationContext());

		client = new SetClient().getclient();
		if(connectionDetector.isConnectedToInternet())
		new ODeskAuthorizeTask().execute();
		else
			toast.showToastid(R.string.s_i_c);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		initUI();
		buildEvents();

	}

	//This function is used to initialize the variables
	private void initUI()
	{
		webview = (WebView)findViewById(R.id.webview);
		get_token = (Button)findViewById(R.id.get_token);
		loadurl = (Button)findViewById(R.id.loadurl);
	}

	//It is used to build the events
	private void buildEvents()
	{
		get_token.setOnClickListener(this);
		loadurl.setOnClickListener(this);
	}

	/*This is used for performing click operation*/
	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.get_token:
			getAccessToken();
			break;
		case R.id.loadurl:
			if(connectionDetector.isConnectedToInternet())
			loadURlinWebview();
			else
				toast.showToastid(R.string.s_i_c);
			break;
		}	
	}

	@Override
	protected void onRestart() 
	{
		if(connectionDetector.isConnectedToInternet())
			new ODeskAuthorizeTask().execute();
		else
			toast.showToastid(R.string.s_i_c);
	    super.onRestart(); 
	}
	
	/*URL is loaded here*/
	private void loadURlinWebview() 
	{
		webview.loadUrl(authzUrlcallback);
		webview.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) 
			{
				loadurl.setVisibility(View.GONE);
			}
		});

	}

	/*This is used for generating the token*/
	private void getAccessToken() 
	{
		String url = webview.getUrl();

		try 
		{
			if(url.length()!=0)
			{
				if(url.contains("oauth_verifier"))
				{
					List<String> items = Arrays.asList(url.split("&"));

					verifier = Arrays.asList(items.get(1).toString().split("="));
					if(connectionDetector.isConnectedToInternet())
					new ODeskRetrieveAccessTokenTask().execute(verifier.get(1).toString());
					else
						toast.showToast(getResources().getString(R.string.s_i_c));
				}
				else
				{
					toast.showToast("Please Login and generate valid url");
				}
			}
			else
			{
				toast.showToast("Please Login and generate valid url");
			}	
		} 
		catch (NullPointerException e) {
			// TODO: handle exception
		}
		
	}


	class ODeskRetrieveAccessTokenTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			String verifier1 = params[0];

			HashMap<String, String> token = client.getAccessTokenSet(verifier1);
			client.setTokenWithSecret(token.get("token"), token.get("secret"));


			// Save token/secret in preferences
			prefs.edit().putString("token", token.get("token"))
			.putString("secret", token.get("secret"))
			.commit();

			return "ok - token is: " + prefs.getString("token", null);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) 
			{
				startActivity(new Intent(LoginActivity.this,MainActivity.class));
			}
		}
	}

	/*This AsyncTask is used to get token URL and callback URL*/
	class ODeskAuthorizeTask extends AsyncTask<Void, Void, String> 
	{
		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Url....");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			authzUrl = client.getAuthorizationUrl();
			authzUrlcallback = authzUrl+"&oauth_callback=http://roopasoft.com/odesk.html";

			return authzUrlcallback;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) 
			{
				webview.loadUrl(authzUrlcallback);
				webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); 

				webview.setWebViewClient(new WebViewClient() {

					public void onPageFinished(WebView view, String url) 
					{
						loadurl.setVisibility(View.GONE);
					}
				});
				
				_dialog.dismiss();
			}
		}
	}

}
