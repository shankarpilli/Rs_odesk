package com.roopasoft.notifications;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootServiceReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
        
		Intent intent1 = new Intent(context, CheckNotification.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),30*1000, pendingIntent);

	}
}
