package com.roopasoft.messages;

import java.util.HashMap;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oDesk.api.OAuthClient;
import com.oDesk.api.Routers.Messages.Mc;
import com.roopasoft.messages.MessageThreadActivity.ODESkgetThreadTask;
import com.roopasoft.network.ConnectionDetector;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;
import com.roopasoft.util.ShowToast;

public class MessageThreadReplyActivity extends Activity implements OnClickListener
{

	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;

	Intent intent;
	String id = "";
	Context context;
	EditText reply;
	TextView tx_name;
	Button send;
	String replyMsg="";
	Font font;
	ConnectionDetector connectionDetector;
	ShowToast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_reply);
		context = this;
		getThreadId();
		font = new Font(context);
		toast = new ShowToast(getApplicationContext());
		connectionDetector = new ConnectionDetector(context);
		initUI();
		binEvents();
		setidAndClient();

	}

	private void binEvents() 
	{
		send.setOnClickListener(this);
	}

	/*We set client value here and that will used for getting messages*/
	private void setidAndClient() 
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString(Config.KEY_TOKEN, null);//We set token value here 
		tokenSecret = prefs.getString(Config.KEY_SECRET, null);//We set secret value here
		myId = prefs.getString(Config.KEY_ID, null);//We set id value here
		myName = prefs.getString(Config.KEY_NAME, null);
		tx_name.setText(""+myName);

		client = new SetClient().getclient();
	}


	/*This method is used for getting thread id*/
	private void getThreadId()
	{
		intent = getIntent();
		id = intent.getStringExtra(Config.KEY_THREADID);
	}

	/*It is used for initialization*/
	private void initUI() 
	{
		reply = (EditText)findViewById(R.id.reply);
		send = (Button)findViewById(R.id.send);
		tx_name = (TextView)findViewById(R.id.tx_name);

		Display display = getWindowManager().getDefaultDisplay(); 
		int height = display.getHeight();

		reply.setHeight((82 *height) / 100 );

		reply.setTypeface(font.typeface_normal_SEGOEUI());
		send.setTypeface(font.typeface_normal_SEGOEUI());
		tx_name.setTypeface(font.typeface_normal_SEGOEUI());
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.send:
			sendReply();
			break;
		}
	}

	private void sendReply()
	{
		if(isValidateFields())
		{
			replyMsg = reply.getText().toString();

			if(connectionDetector.isConnectedToInternet())
				new ODESksendReplyTask().execute();
			else
				toast.showToastid(R.string.s_i_c);
		}
	}

	class ODESksendReplyTask extends AsyncTask<Void, Void, String>
	{
		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Sending Reply...");
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
			HashMap<String, String> params2 = new HashMap<String, String>();
			params2.put(Config.KEY_BODY, replyMsg);
			try {
				mc.replyToThread(myId,id,params2);
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
				_dialog.dismiss();
				Toast.makeText(MessageThreadReplyActivity.this, "Successfully sent",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MessageThreadReplyActivity.this,MessagesActivity.class);
				startActivity(intent);
			}
		}
	}


	/* check the validation in the fields*/
	private boolean isValidateFields()
	{
		if (TextUtils.isEmpty(reply.getText().toString()))
		{
			reply.setError(" Enter Repply");
			reply.requestFocus();
			return false;
		}
		return true;
	}

}
