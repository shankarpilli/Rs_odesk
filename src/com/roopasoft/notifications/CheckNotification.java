package com.roopasoft.notifications;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.oDesk.api.OAuthClient;
import com.oDesk.api.Routers.Messages.Mc;
import com.roopasoft.messages.MessagesActivity;
import com.roopasoft.messages.MessgaesTrayContentInfo;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;
import com.roopasoft.util.SetClient;

public class CheckNotification extends BroadcastReceiver{

	private NotificationManager mNotificationManager;
	Context context2;

	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	String s_check_sounds="",s_check_vibration="",s_check_light_indication="";
	MessgaesTrayContentInfo info;
	List<MessgaesTrayContentInfo> list;
	ConnectionDetector connectionDetector;
	//ShowToast toast;

	@Override
	public void onReceive(Context context, Intent intent) {
		context2 = context;
		//toast = new ShowToast(context);
		connectionDetector = new ConnectionDetector(context);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Config.KEY_TOKEN, null);
		String tokenSecret = prefs.getString(Config.KEY_SECRET, null);
		
		if(token != null && tokenSecret != null)
		{
		callNotification();
		}
		
	}

	private void callNotification() 
	{

		//Toast.makeText(context2, "Stared",Toast.LENGTH_SHORT).show();

		prefs = PreferenceManager.getDefaultSharedPreferences(context2);
		token = prefs.getString(Config.KEY_TOKEN, null);//We set token value here 
		tokenSecret = prefs.getString(Config.KEY_SECRET, null);//We set secret value here
		myId = prefs.getString(Config.KEY_ID, null);//We set id value here
		myName = prefs.getString(Config.KEY_NAME, null);

		client = new SetClient().getclient();
		if(connectionDetector.isConnectedToInternet())
			new ODeskgetDetailsTask().execute();
		else
		{
			//toast.showToastid(R.string.s_i_c);
		}

	}

	/*This is used to get messages from the Odesk*/
	class ODeskgetDetailsTask extends AsyncTask<Void, Void, String> 
	{
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

				String unread = jsonmsgthead.getString(Config.KEY_UNREAD).toString();

				if(Integer.parseInt(jsonmsgthead.getString(Config.KEY_UNREAD).toString())>0)
				{

					jsonArrayThread = jsonmsgthead.getJSONArray(Config.KEY_THREADS);

					list = new ArrayList<MessgaesTrayContentInfo>();

					/*Here we will set all the values into info class and that added to list*/
					for(int i=0;i<Integer.parseInt(jsonmsgthead.getString(Config.KEY_UNREAD).toString());i++)
					{
						info = new MessgaesTrayContentInfo();

						JSONObject jsonobject= (JSONObject) jsonArrayThread.get(i);
						info.setSubject(jsonobject.getString(Config.KEY_SUBJECT));
						info.setMessage(jsonobject.getString(Config.KEY_LAST_POST_PREVIEW));
						info.setThread_id(jsonobject.getString(Config.KEY_ID));
						info.setLast_post_id(jsonobject.getString(Config.KEY_LAST_POST_ID));

						JSONArray participantsarray  = jsonobject.getJSONArray(Config.KEY_PARTICIPANTS);
						for(int j=0;j<participantsarray.length();j++)
						{
							JSONObject jsonobject1= (JSONObject) participantsarray.get(j);
							if (jsonobject.getString(Config.KEY_LAST_POST_ID).equals(jsonobject1.get(Config.KEY_LAST_POST_ID))) {
								info.setOppname(jsonobject1.getString(Config.KEY_FIRST_NAME));
							}
						}

						list.add(info);
					}
					mNotificationManager = (NotificationManager) context2.getSystemService(Context.NOTIFICATION_SERVICE);

					Intent notificationIntent = new Intent(context2, MessagesActivity.class);
					notificationIntent.setAction(Intent.ACTION_MAIN);
					notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

					PendingIntent contentIntent = PendingIntent.getActivity(context2, 0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);  

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context2);
					Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					
					
					mBuilder.setContentIntent(contentIntent);
					mBuilder.setSmallIcon(R.drawable.ic_launcher);
					
					s_check_sounds = prefs.getString(Config.KEY_SOUNDS_STATUS, "");
					s_check_vibration = prefs.getString(Config.KEY_VIBRATION_STATUS, "");
					s_check_light_indication = prefs.getString(Config.KEY_LIGHT_INDICATION_STATUS, "");
					
					if(s_check_sounds.equals(Config.KEY_CHECKED))
						mBuilder.setSound(alarmSound);
					if(s_check_vibration.equals(Config.KEY_CHECKED))
						mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
					if(s_check_light_indication.equals(Config.KEY_CHECKED))
						mBuilder.setLights(Color.RED, 3000, 3000);
					
					mBuilder.setAutoCancel(true);

					for (int i = 0; i < list.size(); i++)
					{
						if(info.getOppname()!=null)
						{
							mBuilder.setContentTitle("Message from " + info.getOppname());
							mBuilder.setContentText(""+info.getSubject());	
						}
					}
					
					mNotificationManager.notify(0,mBuilder.build());
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
		}
	}
}