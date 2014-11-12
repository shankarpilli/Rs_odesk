package com.roopasoft.invitations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oDesk.api.OAuthClient;
import com.roopasoft.rs_odesk.R;
import com.roopasoft.util.Config;
import com.roopasoft.util.Font;
import com.roopasoft.util.SetClient;

public class InvitationsActivity extends Activity
{
	OAuthClient client;
	SharedPreferences prefs;
	String token,tokenSecret,myId,myName;
	Context context;

	TextView tx_name;
	LinearLayout ll_invitations_list;
	OfferInfo info;
	List<OfferInfo> offerslist;
	Font font;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_invitations);
		context = this;
		font = new Font(context);
		initUi();
		showInvitations();
	}

	/*It is used to show all the invitations*/
	private void showInvitations() 
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		token = prefs.getString("token", null);//We set token value here 
		tokenSecret = prefs.getString("secret", null);//We set secret value here
		myId = prefs.getString("id", null);//We set id value here
		myName = prefs.getString("name", null);
		tx_name.setText(""+myName);

		client = new SetClient().getclient();
		new ODeskgetInvitationsTask().execute();		
	}

	/*This is used for the initialization*/
	private void initUi() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);
		ll_invitations_list = (LinearLayout)findViewById(R.id.ll_invitations_list);
	}
	class ODeskgetInvitationsTask extends AsyncTask<Void, Void, String> 
	{

		public ProgressDialog _dialog;

		@Override
		protected void onPreExecute() 
		{
			_dialog = new ProgressDialog(context);
			_dialog.setMessage("Getting Invitations From Odesk");
			_dialog.setIndeterminate(true);
			_dialog.setCancelable(false);
			_dialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Void... params)
		{
			client.setTokenWithSecret(token,tokenSecret);

			Offers offers = new Offers(client);
			HashMap<String, String> params1 = new HashMap<String, String>();
			params1.put("status", "new");

			try {
				JSONObject jsonInvitations = offers.getList(params1);
				JSONObject jsonoffer  = jsonInvitations.getJSONObject("offers");
				JSONObject jsonlister  = jsonoffer.getJSONObject("lister");

				String total_items = jsonlister.getString("total_items");
				int total_items_length=total_items.length();

				if(total_items_length>0)
				{
					offerslist = new ArrayList<OfferInfo>();

					JSONArray jsonArray = jsonoffer.getJSONArray("offers");
					for (int i = 0; i < jsonArray.length(); i++) 
					{
						info = new OfferInfo();
						JSONObject jsonobject= (JSONObject) jsonArray.get(i);
						info.setRid(jsonobject.getString("rid"));
						info.setTitle(jsonobject.getString("title"));
						info.setType(jsonobject.getString("type"));

						offerslist.add(info);
					}
				}
				else
				{
					offerslist = new ArrayList<OfferInfo>();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
				if(offerslist.size()>0)
				{
					for(final OfferInfo info : offerslist)
					{
						LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.offers_item, null);

						TextView title = (TextView)linearLayout.findViewById(R.id.title);
						TextView type = (TextView)linearLayout.findViewById(R.id.type);

						title.setText(info.getTitle());
						type.setText(info.getType());

						title.setTypeface(font.typeface_normal_SEGOEUI());
						type.setTypeface(font.typeface_normal_SEGOEUI());

						/*offeritem.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Intent intent = new Intent(InvitationsActivity.this,SpecificOfferActivity.class);
								intent.putExtra("rid",info.getRid());
								startActivity(intent);
							}
						});*/
						ll_invitations_list.addView(linearLayout);
					}
				}
				else
				{
					LinearLayout ll_message_item = (LinearLayout)getLayoutInflater().inflate(R.layout.empty_norecords_item, null);
					TextView tx_noitems = (TextView)ll_message_item.findViewById(R.id.tx_noitems);
					tx_noitems.setText(Config.KEY_OFFERS_EMPTY);
					tx_noitems.setTypeface(font.typeface_bold_SEGOEUI());
					ll_invitations_list.addView(ll_message_item);
				}
				_dialog.dismiss();
			}
		}
	}
}
