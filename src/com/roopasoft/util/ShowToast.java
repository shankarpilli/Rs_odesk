package com.roopasoft.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ShowToast {

	Context context;

	public ShowToast(Context context)
	{
		this.context = context;
	}

	public void showToastid(int msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public void showToast(String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	public void showLog(String tag, String msg)
	{
		Log.d(tag, msg);
	}
}
