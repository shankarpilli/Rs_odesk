package com.roopasoft.notifications;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.oDesk.api.Routers.Messages.Mc;
import com.roopasoft.messages.MessageThreadActivity;
import com.roopasoft.messages.MessgaesTrayContentInfo;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class NotificationsActivity extends Activity
{
	AlarmManager am;
	Context context;
	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	ConnectionDetector connectionDetector;
	ShowToast toast;
	TextView messages,tx_name,tx_messages;
	LinearLayout messages_linearlayout,ll_message_item;
	MessgaesTrayContentInfo info;
	List<MessgaesTrayContentInfo> list;
	Font font;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.messages_list);
		context = this;
		connectionDetector = new ConnectionDetector(context);
		toast = new ShowToast(getApplicationContext());

		font = new Font(context);
		initUI();
		showNotifications();
	}

	/*It is used to show all the messages*/
	private void showNotifications()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString(Config.KEY_TOKEN, null);//We set token value here 
		tokenSecret = prefs.getString(Config.KEY_SECRET, null);//We set secret value here
		myId = prefs.getString(Config.KEY_ID, null);//We set id value here
		myName = prefs.getString(Config.KEY_NAME, null);
		tx_name.setText(""+myName);

		client = new SetClient().getclient();
		if(connectionDetector.isConnectedToInternet())
			new ODeskgetNotificationsTask().execute();
		else
			toast.showToastid(R.string.s_i_c);
	}

	/*It is used for the initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);
		tx_messages = (TextView)findViewById(R.id.tx_messages);
		messages_linearlayout = (LinearLayout)findViewById(R.id.messages_linearlayout);

		tx_messages.setText(Config.KEY_NOTIFICATIONS);
		
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());
		tx_messages.setTypeface(font.typeface_bold_SEGOEUI());
	}


	/*This is used to get messages from the Odesk*/
	class ODeskgetNotificationsTask extends AsyncTask<Void, Void, String> 
	{

		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Notifications From Odesk");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}


		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);

			JSONObject jsonmsgthead = null;
			JSONArray jsonArrayThread = null; 
			JSONObject jsonObject = null;

			try {
				// Get messages
				Mc mc = new Mc(client);

				jsonObject = mc.getTrayByType(myId,Config.KEY_INBOX);
				jsonmsgthead = jsonObject.getJSONObject(Config.KEY_CURRENT_TRAY);
				jsonArrayThread = jsonmsgthead.getJSONArray(Config.KEY_THREADS);
				int unread = Integer.parseInt(jsonmsgthead.getString(Config.KEY_UNREAD));

				int threadlangth = jsonArrayThread.length();
				list = new ArrayList<MessgaesTrayContentInfo>();

				/*Here we will set all the values into info class and that added to list*/
				for(int i=0;i<threadlangth;i++)
				{
					info = new MessgaesTrayContentInfo();

					JSONObject jsonobject= (JSONObject) jsonArrayThread.get(i);
					info.setSubject(jsonobject.getString(Config.KEY_SUBJECT));
					info.setMessage(jsonobject.getString(Config.KEY_LAST_POST_PREVIEW));
					info.setThread_id(jsonobject.getString(Config.KEY_ID));
					if( i < unread)		
					{			
						info.setUnread(Config.KEY_1);
					} else
					{
						info.setUnread(Config.KEY_0);
					}

					JSONArray participantsarray  = jsonobject.getJSONArray(Config.KEY_PARTICIPANTS);
					for(int j=0;j<participantsarray.length();j++)
					{
						JSONObject jsonobject1= (JSONObject) participantsarray.get(j);
						info.setOppname(jsonobject1.getString(Config.KEY_FIRST_NAME));
					}

					list.add(info);
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

				if(list.size()>0)
				{
					/*Getting that list and displayed here*/
					for(final MessgaesTrayContentInfo info : list)
					{
						if(info.getUnread()==Config.KEY_1)
						{
							LinearLayout ll_message_item = (LinearLayout)getLayoutInflater().inflate(R.layout.message_item, null);

							LinearLayout msgitem = (LinearLayout)ll_message_item.findViewById(R.id.msgitem);
							TextView name = (TextView)ll_message_item.findViewById(R.id.name);
							TextView subject = (TextView)ll_message_item.findViewById(R.id.subject);
							TextView message = (TextView)ll_message_item.findViewById(R.id.message);


							ll_message_item.setBackgroundColor(Color.parseColor("#DEDCDC"));

							name.setText(info.getOppname());
							subject.setText(info.getSubject());
							message.setText(info.getMessage());

							name.setTypeface(font.typeface_normal_SEGOEUI());
							subject.setTypeface(font.typeface_bold_SEGOEUI());
							message.setTypeface(font.typeface_normal_SEGOEUI());

							msgitem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v)
								{
									Intent intent = new Intent(NotificationsActivity.this,MessageThreadActivity.class);
									intent.putExtra(com.roopasoft.util.Config.KEY_ID,info.getThread_id());
									startActivity(intent);
								}
							});
							messages_linearlayout.addView(ll_message_item);
						}
						else
						{
							LinearLayout ll_message_item = (LinearLayout)getLayoutInflater().inflate(R.layout.empty_norecords_item, null);
							TextView tx_noitems = (TextView)ll_message_item.findViewById(R.id.tx_noitems);
							tx_noitems.setTypeface(font.typeface_bold_SEGOEUI());
							messages_linearlayout.addView(ll_message_item);
							break;
						}
					}
				}
				else
				{
					LinearLayout ll_message_item = (LinearLayout)getLayoutInflater().inflate(R.layout.empty_norecords_item, null);
					TextView tx_noitems = (TextView)ll_message_item.findViewById(R.id.tx_noitems);
					tx_noitems.setTypeface(font.typeface_bold_SEGOEUI());
					messages_linearlayout.addView(ll_message_item);
				}
				_dialog.dismiss();
			}
		}
	}

}
