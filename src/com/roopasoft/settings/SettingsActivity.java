package com.roopasoft.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.roopasoft.rs_odesk.R;

public class SettingsActivity extends Activity implements OnCheckedChangeListener
{
	TextView tx_name,tx_s_sounds,tx_s_vibration,tx_s_light_indication,tx_settings;
	CheckBox check_sounds,check_vibration,check_light_indication;
	Context context;
	SharedPreferences prefs;
	String myId,myName;
	Typeface typeface,typeface_b;
	String s_check_sounds="",s_check_vibration="",s_check_light_indication="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		context = this;
		typeface = Typeface.createFromAsset(getAssets(), "Fonts/SEGOEUI.TTF");
		typeface_b = Typeface.createFromAsset(getAssets(), "Fonts/SEGOEUIB.TTF");

		initUI();
		showUserName();
		bindEvents();
	}

	/*It is used to bind the events*/
	private void bindEvents() 
	{
		check_sounds.setOnCheckedChangeListener(this);
		check_vibration.setOnCheckedChangeListener(this);
		check_light_indication.setOnCheckedChangeListener(this);
	}

	/*This method is used for show the name*/
	private void showUserName()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		myId = prefs.getString("id", null);//We set id value here
		myName = prefs.getString("name", null);
		tx_name.setText(""+myName);
		
		s_check_sounds = prefs.getString("sounds_status", "");
		s_check_vibration = prefs.getString("vibration_status", "");
		s_check_light_indication = prefs.getString("light_indication_status", "");
		
		if(s_check_sounds.equals("checked"))
			check_sounds.setChecked(true);
		if(s_check_vibration.equals("checked"))
			check_vibration.setChecked(true);
		if(s_check_light_indication.equals("checked"))
			check_light_indication.setChecked(true);
	}

	/*It is used for initialization*/
	private void initUI() 
	{
		tx_name = (TextView)findViewById(R.id.tx_name);
		tx_settings = (TextView)findViewById(R.id.tx_settings);

		tx_s_sounds = (TextView)findViewById(R.id.tx_s_sounds);
		tx_s_vibration = (TextView)findViewById(R.id.tx_s_vibration);
		tx_s_sounds = (TextView)findViewById(R.id.tx_s_sounds);

		check_sounds = (CheckBox)findViewById(R.id.check_sounds);
		check_vibration = (CheckBox)findViewById(R.id.check_vibration);
		check_light_indication = (CheckBox)findViewById(R.id.check_light_indication);

		tx_name.setTypeface(typeface);
		tx_settings.setTypeface(typeface_b);
		tx_s_sounds.setTypeface(typeface);
		tx_s_vibration.setTypeface(typeface);
		tx_s_sounds.setTypeface(typeface);
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		switch (buttonView.getId()) {
		case R.id.check_sounds:
			if(s_check_sounds=="checked")
			{
				s_check_sounds="unchecked";
				prefs.edit().putString("sounds_status", s_check_sounds).commit();
			}
			else
			{
				s_check_sounds = "checked";
				prefs.edit().putString("sounds_status", s_check_sounds).commit();
			}
			break;
		case R.id.check_vibration:
			if(s_check_vibration=="checked")
			{
				s_check_vibration="unchecked";
				prefs.edit().putString("vibration_status", s_check_vibration).commit();
			}
			
			else
			{
				s_check_vibration = "checked";
				prefs.edit().putString("vibration_status", s_check_vibration).commit();
			}
			break;
		case R.id.check_light_indication:
			if(check_light_indication.equals("checked"))
			{
				s_check_light_indication="unchecked";
				prefs.edit().putString("light_indication_status", s_check_light_indication).commit();
				showPref();
			}
			else
			{
				s_check_light_indication = "checked";
				prefs.edit().putString("light_indication_status", s_check_light_indication).commit();
				showPref();
			}
			break;
		}
	}
	public void showPref()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String Check_status = prefs.getString("light_indication_status", null);
		Toast.makeText(SettingsActivity.this, Check_status, Toast.LENGTH_SHORT).show();	
	}
	
}
