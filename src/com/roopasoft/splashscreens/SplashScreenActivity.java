package com.roopasoft.splashscreens;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

import com.roopasoft.login.LoginActivity;
import com.roopasoft.notifications.CheckNotification;
import com.roopasoft.rs_odesk.MainActivity;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;

public class SplashScreenActivity extends Activity
{
	SharedPreferences prefs;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreen);
		context = this;
		setHandler();
	}
	//This function is allow to perform the task after some time
	private void setHandler() 
	{
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				// Read the prefs to see if we have token
				prefs = PreferenceManager.getDefaultSharedPreferences(context);
				String token = prefs.getString(Config.KEY_TOKEN, null);
				String tokenSecret = prefs.getString(Config.KEY_SECRET, null);

				/*If this condition is okay the user has goes to main activity otherwise he redirect to login*/
				if(token != null && tokenSecret != null)
				{
					sendBroadcastMethod(context);
					
					Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);//It is used to play animation to show the main activity
					finish();
				}
				else
				{
					/*It used to navigate the screen from splash to login*/
					Intent intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);//It is used to play animation to show the main activity
					finish();

				}
			}
		}, Config.SPLASH_TIME_OUT);	
	}
	
	/*Here we are setting messages notification by using Broadcast receiver*/
	private void sendBroadcastMethod(Context context)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
        
		Intent intent = new Intent(this, CheckNotification.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),30*1000, pendingIntent);
	}
	
}
