package com.roopasoft.rs_odesk;


import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.oDesk.api.Routers.Messages.Mc;
import com.oDesk.api.Routers.Organization.Users;
import com.roopasoft.invitations.InvitationsActivity;
import com.roopasoft.jobs.JobsMainActivity;
import com.roopasoft.login.LoginActivity;
import com.roopasoft.messages.MessagesActivity;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.notifications.NotificationsActivity;
import com.roopasoft.profile.ProfileActivity;
import com.roopasoft.settings.SettingsActivity;
import com.roopasoft.util.Config;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class MainActivity extends Activity implements OnClickListener
{

	OAuthClient client;
	SharedPreferences prefs;
	Context context;
	String token,tokenSecret;
	TextView tx_name,tx_jobs,tx_messages,tx_notifications,tx_invitations,tx_profile,tx_settings,tx_messages_count;
	String myId="",myEmail="",myName="";
	String userssdfs;
	RelativeLayout jobs_layout,messages_layout,settings_layout,profile_layout,invitations_layout,notifications_layout;
	Intent intent;
	View view;
	int unread;
	ImageView logout;
	Font font;
	ShowToast toast;
	ConnectionDetector connectionDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		getTokenAndSecretFromPrefs();
		font = new Font(context);
		toast = new ShowToast(getApplicationContext());

		connectionDetector = new ConnectionDetector(context);
		initUI();
		bindEvents();
		if(connectionDetector.isConnectedToInternet())
			ShowDetails(new ODeskgetDetailsTask().execute());
		else
			toast.showToastid(R.string.s_i_c);
	}

	/*Used for binding events*/
	private void bindEvents() 
	{
		jobs_layout.setOnClickListener(this);
		messages_layout.setOnClickListener(this);
		settings_layout.setOnClickListener(this);
		profile_layout.setOnClickListener(this);
		invitations_layout.setOnClickListener(this);
		notifications_layout.setOnClickListener(this);

		logout.setOnClickListener(this);
	}

	/*This method is used for displaying id,email and name*/
	private void ShowDetails(AsyncTask<Void, Void, String> execute) 
	{
		tx_name.setText(""+myName);
	}

	/*This is used for initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);

		jobs_layout = (RelativeLayout)findViewById(R.id.jobs_layout);
		messages_layout = (RelativeLayout)findViewById(R.id.messages_layout);
		settings_layout = (RelativeLayout)findViewById(R.id.settings_layout);
		profile_layout = (RelativeLayout)findViewById(R.id.profile_layout);
		invitations_layout = (RelativeLayout)findViewById(R.id.invitations_layout);
		notifications_layout = (RelativeLayout)findViewById(R.id.notifications_layout);

		tx_jobs = (TextView)findViewById(R.id.tx_jobs);
		tx_messages = (TextView)findViewById(R.id.tx_messages);
		tx_notifications = (TextView)findViewById(R.id.tx_notifications);
		tx_invitations = (TextView)findViewById(R.id.tx_invitations);
		tx_profile = (TextView)findViewById(R.id.tx_profile);
		tx_settings = (TextView)findViewById(R.id.tx_settings);

		tx_messages_count = (TextView)findViewById(R.id.tx_messages_count);

		logout= (ImageView)findViewById(R.id.logout);
		logout.setVisibility(View.VISIBLE);

		tx_name.setTypeface(font.typeface_normal_SEGOEUI());

		tx_jobs.setTypeface(font.typeface_normal_SEGOEUI());
		tx_messages.setTypeface(font.typeface_normal_SEGOEUI());
		tx_notifications.setTypeface(font.typeface_normal_SEGOEUI());
		tx_invitations.setTypeface(font.typeface_normal_SEGOEUI());
		tx_profile.setTypeface(font.typeface_normal_SEGOEUI());
		tx_settings.setTypeface(font.typeface_normal_SEGOEUI());

		tx_messages_count.setTypeface(font.typeface_normal_SEGOEUI());
	}

	/*This is used to get the token and secret from SharedPreferences*/
	private void getTokenAndSecretFromPrefs()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString("token", null);
		tokenSecret = prefs.getString("secret", null);
	}


	/*This is used to get your details from the Odesk*/
	class ODeskgetDetailsTask extends AsyncTask<Void, Void, String> 
	{

		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Name");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}


		@Override
		protected String doInBackground(Void... params)
		{
			client = new SetClient().getclient();
			client.setTokenWithSecret(token,tokenSecret);

			JSONObject jsonUserDetails = null;

			try {
				// Get info of authenticated user
				Users users = new Users(client);
				jsonUserDetails = users.getMyInfo();

				// get my uid
				myId = null;
				try {
					JSONObject user = jsonUserDetails.getJSONObject(Config.KEY_USER);
					myId = user.getString(Config.KEY_ID);
					myEmail = user.getString(Config.KEY_EMAIL);
					myName = user.getString(Config.KEY_FIRST_NAME);

					prefs.edit().putString(Config.KEY_ID, myId)
					.putString(Config.KEY_EMAIL,myEmail)
					.putString(Config.KEY_NAME,myName)
					.commit();

					Mc mc = new Mc(client);

					JSONObject	jsonObject = mc.getTrayByType(myId,Config.KEY_INBOX);
					JSONObject jsonmsgthead = jsonObject.getJSONObject(Config.KEY_CURRENT_TRAY);
					unread = Integer.parseInt(jsonmsgthead.getString(Config.KEY_UNREAD));

				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

			return myId;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			if (_dialog.isShowing()) 
			{
				tx_name.setText(""+myName);
				tx_messages_count.setText(""+unread);
				_dialog.dismiss();
			}

		}
	}

	/*Here we perform click operations*/
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.jobs_layout:
			view = jobs_layout;
			intent = new Intent(this, JobsMainActivity.class);
			playAnimation(view);
			break;
		case R.id.messages_layout:
			view = messages_layout;
			intent = new Intent(this, MessagesActivity.class);
			playAnimation(view);
			break;
		case R.id.settings_layout:
			view = settings_layout;
			intent = new Intent(this, SettingsActivity.class);
			playAnimation(view);
			break;
		case R.id.profile_layout:
			view = profile_layout;
			intent = new Intent(this, ProfileActivity.class);
			playAnimation(view);
			break;
		case R.id.invitations_layout:
			view = invitations_layout;
			intent = new Intent(this, InvitationsActivity.class);
			playAnimation(view);
			break;
		case R.id.notifications_layout:
			view = notifications_layout;
			intent = new Intent(this, NotificationsActivity.class);
			playAnimation(view);
			break;
		case R.id.logout:
			logoout();
			break;
		}
	}

	/*It is used to logout from the account*/
	private void logoout()
	{
		Editor editor = getSharedPreferences("com.roopasoft.rs_odesk_preferences", Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		
		clearApplicationData();
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
	}

	/*It is used to clear the data in the application*/
	public void clearApplicationData() {
		//File cache = getCacheDir();

		File[] chahces = {getExternalCacheDir(),getFilesDir()};

		for (int i = 0; i < chahces.length; i++) 
		{
			File appDir = new File(chahces[i].getParent());
			if (appDir.exists()) 
			{
				String[] children = appDir.list();
				for (String s : children)
				{
					if (!s.equals("lib")) 
					{
						deleteDir(new File(appDir, s));
					}
				}
			}
		}
	}
	public static boolean deleteDir(File dir) 
	{
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	/*This method is used for playing animation*/
	private void playAnimation(View view) {
		final Interpolator decelerator = new DecelerateInterpolator();

		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f);
		animator.setDuration(1000);
		animator.setInterpolator(decelerator);
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				startActivity(intent);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animator.start();
	}
}
