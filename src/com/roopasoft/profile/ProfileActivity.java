package com.roopasoft.profile;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.oDesk.api.Auth;
import com.oDesk.api.OAuthClient;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class ProfileActivity extends Activity
{

	TextView tx_name,tx_p_firstname,tx_firstname,tx_p_lastname,tx_lastname,tx_p_timezone,tx_timezone;
	TextView tx_p_city,tx_city,tx_p_state,tx_state,tx_p_country,tx_country;

	Context context;
	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	String s_fname="",s_lname="",s_timezone="",s_city="",s_state="",s_country="",s_img_url="";
	WebView web_image;
	Font font;
	ShowToast toast;
	ConnectionDetector connectionDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile);
		context = this;
		font = new Font(context);
		toast = new ShowToast(context);
		connectionDetector = new ConnectionDetector(context);

		initUI();
		showProfile();
	}

	/*This method is used for show the profile*/
	private void showProfile()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString("token", null);//We set token value here 
		tokenSecret = prefs.getString("secret", null);//We set secret value here
		myId = prefs.getString("id", null);//We set id value here
		myName = prefs.getString("name", null);
		tx_name.setText(""+myName);

		client = new SetClient().getclient();
		if(connectionDetector.isConnectedToInternet())
			new ODeskgetProfileTask().execute();
		else
			toast.showToastid(R.string.s_i_c);
		
	}

	/*It is used for the initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);

		tx_p_firstname = (TextView)findViewById(R.id.tx_p_firstname);
		tx_p_lastname = (TextView)findViewById(R.id.tx_p_lastname);
		tx_p_timezone = (TextView)findViewById(R.id.tx_p_timezone);
		tx_p_city = (TextView)findViewById(R.id.tx_p_city);
		tx_p_state = (TextView)findViewById(R.id.tx_p_state);
		tx_p_country = (TextView)findViewById(R.id.tx_p_country);

		tx_firstname = (TextView)findViewById(R.id.tx_firstname);
		tx_lastname = (TextView)findViewById(R.id.tx_lastname);
		tx_timezone = (TextView)findViewById(R.id.tx_timezone);
		tx_city = (TextView)findViewById(R.id.tx_city);
		tx_state = (TextView)findViewById(R.id.tx_state);
		tx_country = (TextView)findViewById(R.id.tx_country);

		web_image = (WebView)findViewById(R.id.web_image);
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());

		tx_p_firstname.setTypeface(font.typeface_bold_SEGOEUI());
		tx_p_lastname.setTypeface(font.typeface_bold_SEGOEUI());
		tx_p_timezone.setTypeface(font.typeface_bold_SEGOEUI());
		tx_p_city.setTypeface(font.typeface_bold_SEGOEUI());
		tx_p_state.setTypeface(font.typeface_bold_SEGOEUI());
		tx_p_country.setTypeface(font.typeface_bold_SEGOEUI());

		tx_firstname.setTypeface(font.typeface_normal_SEGOEUI());
		tx_lastname.setTypeface(font.typeface_normal_SEGOEUI());
		tx_timezone.setTypeface(font.typeface_normal_SEGOEUI());
		tx_city.setTypeface(font.typeface_normal_SEGOEUI());
		tx_state.setTypeface(font.typeface_normal_SEGOEUI());
		tx_country.setTypeface(font.typeface_normal_SEGOEUI());
	}

	/*This is used to get messages from the Odesk*/
	class ODeskgetProfileTask extends AsyncTask<Void, Void, String> 
	{

		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Profile From Odesk");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);
			JSONObject jsonprofilethread = null;


			try {
				Auth auth = new Auth(client);
				jsonprofilethread = auth.getUserInfo();

				JSONObject jsonauth = jsonprofilethread.getJSONObject("auth_user"); 

				s_fname = jsonauth.getString("first_name");
				s_lname = jsonauth.getString("last_name");
				s_timezone = jsonauth.getString("timezone");

				JSONObject jsoninfo = jsonprofilethread.getJSONObject("info");

				s_img_url = jsoninfo.getString("portrait_100_img");

				JSONObject jsonlocation = jsoninfo.getJSONObject("location");

				s_city = jsonlocation.getString("city");
				s_state = jsonlocation.getString("state");
				s_country = jsonlocation.getString("country");


			} catch (Exception e) {
				// TODO: handle exception
			}

			return myId;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			if (_dialog.isShowing()) 
			{

				web_image.loadUrl(s_img_url);

				tx_firstname.setText(": "+s_fname);
				tx_lastname.setText(": "+s_lname);
				tx_timezone.setText(": "+s_timezone);
				tx_city.setText(": "+s_city);
				tx_state.setText(": "+s_state);
				tx_country.setText(": "+s_country);

				_dialog.dismiss();
			}
		}
	}
}
