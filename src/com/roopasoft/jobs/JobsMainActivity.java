package com.roopasoft.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;

public class JobsMainActivity extends Activity implements OnClickListener
{
	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	Context context;
	String q=".net";

	TextView tx_name;
	EditText ed_searchedtext;
	Button bt_search_jobs,back_top;
	LinearLayout ll_jobs_list;

	JobsMainInfo info;
	List<JobsMainInfo> list;
	Font font;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jobs);
		context = this;
		font = new Font(context);
		
		initUI();
		showJobs();
		buildEvents();
	}

	/*It is used for building events*/
	private void buildEvents()
	{
		bt_search_jobs.setOnClickListener(this);
	}

	/*It is used for the initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);
		ed_searchedtext = (EditText)findViewById(R.id.ed_searchedtext);
		bt_search_jobs = (Button)findViewById(R.id.bt_search_jobs);
		back_top = (Button)findViewById(R.id.back_top);
		ll_jobs_list = (LinearLayout)findViewById(R.id.ll_jobs_list);
		
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());
		ed_searchedtext.setTypeface(font.typeface_normal_SEGOEUI());
		bt_search_jobs.setTypeface(font.typeface_normal_SEGOEUI());
	}


	/*It is used to show all the messages*/
	private void showJobs()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString(Config.KEY_TOKEN, null);//We set token value here 
		tokenSecret = prefs.getString(Config.KEY_SECRET, null);//We set secret value here
		myId = prefs.getString(Config.KEY_ID, null);//We set id value here
		myName = prefs.getString(Config.KEY_NAME, null);
		tx_name.setText(""+myName);

		client = new SetClient().getclient();
		new ODeskgetJobDetailsTask().execute();
	}

	/*This is used to get messages from the Odesk*/
	class ODeskgetJobDetailsTask extends AsyncTask<Void, Void, String> 
	{

		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting jobs From Odesk");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);

			JSONObject jsonobject = null; 

			Search search = new Search(client);
			HashMap<String, String> params1 = new HashMap<String, String>();
			params1.put(Config.KEY_Q, q);

			try {
				jsonobject = search.find(params1);
				JSONArray jbosArrayThread = jsonobject.getJSONArray(Config.KEY_JOBS);

				int jobsArraythereadLength = jbosArrayThread.length();
				list = new ArrayList<JobsMainInfo>();

				/*Here we will set all the values into info class and that added to list*/
				for(int i=0;i<jobsArraythereadLength;i++)
				{
					info = new JobsMainInfo();

					JSONObject jsonArraysingleObject= (JSONObject) jbosArrayThread.get(i);
					info.setTitle(jsonArraysingleObject.getString(Config.KEY_TITLE));
					info.setSnippet(jsonArraysingleObject.getString(Config.KEY_SNIPPET));
					info.setId(jsonArraysingleObject.getString(Config.KEY_URL));
					info.setDuration(jsonArraysingleObject.getString(Config.KEY_WORKLOAD));

					JSONArray skillsarray  = jsonArraysingleObject.getJSONArray(Config.KEY_SKILLS);
					for(int j=0;j<skillsarray.length();j++)
					{
						if(info.getSkills()==null)
						{
							info.setSkills(skillsarray.get(j).toString());						
						}
						else
						{
							info.setSkills(info.getSkills() +","+skillsarray.get(j).toString());
						}
					}
					list.add(info);
				}

			} catch (JSONException e) {
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

				/*Getting that list and displayed here*/
				for(final JobsMainInfo info : list)
				{
					LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.jobs_item, null);

					LinearLayout jobitem = (LinearLayout)linearLayout.findViewById(R.id.jobitem);
					TextView title = (TextView)linearLayout.findViewById(R.id.title);
					TextView durtaion = (TextView)linearLayout.findViewById(R.id.durtaion);
					TextView snippet = (TextView)linearLayout.findViewById(R.id.snippet);
					TextView skills = (TextView)linearLayout.findViewById(R.id.skills);

					durtaion.setText(info.getDuration());
					skills.setText(info.getSkills());
					title.setText(info.getTitle());
					snippet.setText(info.getSnippet());
					
					durtaion.setTypeface(font.typeface_normal_SEGOEUI());
					skills.setTypeface(font.typeface_bold_SEGOEUI());
					title.setTypeface(font.typeface_bold_SEGOEUI());
					snippet.setTypeface(font.typeface_normal_SEGOEUI());
					
					jobitem.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v)
						{
							Intent intent = new Intent(JobsMainActivity.this,JobsDisplayActivity.class);
							intent.putExtra(Config.KEY_JOBID,info.getId());
							startActivity(intent);
						}
					});

					ll_jobs_list.addView(linearLayout);
				}
			}
			_dialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.bt_search_jobs:
			setqValue();
			break;
		case R.id.back_top:
		//	ll_jobs_list.setSelectionAfterHeaderView();
			break;
		}	
	}
	/*Here we will set the query value and pass to AsyncTask */
	private void setqValue()
	{
		if (isValidateFields()) {
			q = ed_searchedtext.getText().toString();
			ll_jobs_list.removeAllViews();			
			new ODeskgetJobDetailsTask().execute();
		}
	}

	/* check the validation in the fields*/
	private boolean isValidateFields()
	{
		if (TextUtils.isEmpty(ed_searchedtext.getText().toString()))
		{
			ed_searchedtext.setError(" Enter Query");
			ed_searchedtext.requestFocus();
			return false;
		}
		return true;
	}

}
