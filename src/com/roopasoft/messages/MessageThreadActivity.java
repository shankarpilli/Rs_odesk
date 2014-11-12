package com.roopasoft.messages;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.oDesk.api.Routers.Messages.Mc;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class MessageThreadActivity extends Activity implements OnClickListener
{
	Intent intent;
	String id="";
	LinearLayout messagesthread_linearlayout;
	TextView subject_text,tx_name;
	Context context;
	Button btn_reply;

	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	MessageThreadInfo info;
	ConnectionDetector connectionDetector;
	ShowToast toast;
	Font font;
	
	String subject="";
	List<MessageThreadInfo> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.messages_thread);
		context = this;
		getThreadId();
		connectionDetector = new ConnectionDetector(context);
		toast = new ShowToast(getApplicationContext());
		font = new Font(context);
		
		initUI();
		bindEvents();
		setidAndClient();

	}

	/*Binding evets here*/
	private void bindEvents() 
	{
		btn_reply.setOnClickListener(this);
	}

	/*It is used for initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);
		subject_text = (TextView)findViewById(R.id.subject_text);
		messagesthread_linearlayout = (LinearLayout)findViewById(R.id.messagesthread_linearlayout);
		btn_reply = (Button)findViewById(R.id.btn_reply);
		
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());
		subject_text.setTypeface(font.typeface_bold_SEGOEUI());
		btn_reply.setTypeface(font.typeface_normal_SEGOEUI());
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
		
		if(connectionDetector.isConnectedToInternet())
			new ODESkgetThreadTask().execute();
		else
			toast.showToastid(R.string.s_i_c);
		
		
	}

	/*This method is used for getting thread id*/
	private void getThreadId()
	{
		intent = getIntent();
		id = intent.getStringExtra("id");
	}

	class ODESkgetThreadTask extends AsyncTask<Void, Void, String>
	{
		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Thread All messages");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);
			
			Mc mc = new Mc(client);
			
			try 
			{
				JSONObject threadDetails = mc.getThreadDetails(myId, id);
				JSONObject innearThread  = threadDetails.getJSONObject("thread");
				subject = innearThread.getString("subject");
				
				JSONArray postsarray = innearThread.getJSONArray("posts");
				JSONArray participantsarray = innearThread.getJSONArray("participants");
				
				list = new ArrayList<MessageThreadInfo>();
				
				for(int i=0;i<postsarray.length();i++)
				{
					info = new MessageThreadInfo();

					JSONObject jsonobject= (JSONObject) postsarray.get(i);
					info.setMessages(jsonobject.getString("content"));
					
					String username = jsonobject.getString("sender_username");
					
					for(int j=0;j<participantsarray.length();j++)
					{
						JSONObject jsonobject1= (JSONObject) participantsarray.get(j);
						String username1 = jsonobject1.getString("username");
						String firstname = jsonobject1.getString("first_name");
						if(username1.equals(username))
						{
							info.setFirstname(firstname);
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
				    subject_text.setText("Subject :  "+subject);
					/*Getting that list and displayed here*/
					for(final MessageThreadInfo info : list)
					{
						LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.messageview_item, null);


						TextView name = (TextView)linearLayout.findViewById(R.id.name);
						TextView message = (TextView)linearLayout.findViewById(R.id.message);

						name.setText(info.getFirstname());
						message.setText(info.getMessages());
						
						name.setTypeface(font.typeface_bold_SEGOEUI());
						message.setTypeface(font.typeface_normal_SEGOEUI());

						messagesthread_linearlayout.addView(linearLayout);
					}
					_dialog.dismiss();
				}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.btn_reply:
			Intent intent = new Intent(MessageThreadActivity.this,MessageThreadReplyActivity.class);
			intent.putExtra("threadId", id);
			startActivity(intent);
			break;
		}
	}
}
