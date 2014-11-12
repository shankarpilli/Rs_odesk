package com.roopasoft.jobs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;

public class JobsDisplayActivity extends Activity
{
	Intent intent;
	String id="";
	Context context;

	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;

	TextView title,category,job_type,budget,snippet,skills_indetail,tx_name;
	JobsDetailsInfo info;
	List<JobsDetailsInfo> list;
	WebView webview;
	Font font;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jobs_item_view);
		context = this;
		getJobId();
		font = new Font(context);
		initUI();
		webview.loadUrl(id);
		
	}
	/*This is used for the initialization*/
	private void initUI() 
	{

		webview = (WebView)findViewById(R.id.webview);
		tx_name = (TextView)findViewById(R.id.tx_name);
		
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());
	}

	/*This method is used for getting job id*/
	private void getJobId()
	{
		intent = getIntent();
		id = intent.getStringExtra("jobid");
		Log.d("String Id ", id);
	}

	/*We set client value here and that will used for getting messages*/
	private void setidAndClient() 
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString("token", null);//We set token value here 
		tokenSecret = prefs.getString("secret", null);//We set secret value here
		myId = prefs.getString("id", null);//We set id value here
		myName = prefs.getString("name", null);

		tx_name.setText(""+myName);

		client = new SetClient().getclient();
		//new ODESkgetJobTask().execute();
	}

	class ODESkgetJobTask extends AsyncTask<Void, Void, String>
	{
		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Job Details");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);
			try 
			{
				Jobs jobs = new Jobs(client);
				JSONObject jobDetails  = jobs.getSpecific(id);

				list = new ArrayList<JobsDetailsInfo>();
				info = new JobsDetailsInfo();

				info.setTitle(jobDetails.getString("title"));
				info.setBudget(jobDetails.getString("budget"));
				info.setCategory(jobDetails.getString("category"));
				info.setJob_type(jobDetails.getString("job_type"));
				info.setSnippet(jobDetails.getString("description"));
				info.setSkills_indetail(jobDetails.getString("skills"));

				list.add(info);
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO Auto-generated method stub
			return null;
		}
		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);

			if (_dialog.isShowing()) 
			{
				title.setText(info.getTitle());
				category.setText(info.getCategory());
				job_type.setText(info.getJob_type());
				budget.setText(info.getBudget());
				snippet.setText(info.getSnippet());
				skills_indetail.setText(info.getSkills_indetail());
			}
			_dialog.dismiss();
		}
	}
}
