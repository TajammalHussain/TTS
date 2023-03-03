package com.umsit.bustrackerdriver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private Button wifi_Connection, gps_Settings, gps_Status, _bAvailCar;
	TextView _tvAvailable, tvBusNo;
	RelativeLayout btnLogout;
	LinearLayout ll1, ll2, body_layout, sliderLayout;

	private boolean bAvailable;

	private double _lat, _long;

	SharedPreferences _sp;
	SharedPreferences.Editor _ed;

	WifiManager wifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initilizeViews();
		initilizeOthers();
		setListeners();

		_sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		_ed = _sp.edit();
	//	bAvailable = _sp.getBoolean("available", false);
		
		String strBus = _sp.getString("bus_no", "AJK111");
		tvBusNo.setText(strBus);
	//	isAvailable();
		startService(new Intent(
				getApplicationContext(),
				com.umsit.bustrackerdriver.services.BusLocationService.class));

		wifi = (WifiManager) this.getApplicationContext().getSystemService(
				Context.WIFI_SERVICE);

		if (!wifi.isWifiEnabled()) {
			wifi_Connection.setBackgroundResource(R.drawable.img_wifi_disable);
			Toast.makeText(this, "On your Wifi", Toast.LENGTH_SHORT).show();
		} else {
			wifi_Connection.setBackgroundResource(R.drawable.img_wifi_enable);
			if (isOnline()) {

				/*// makeJsonObjReq();
				new GetStatus().execute(_sp.getString("driver_email", ""));
				new GetNearDriver().execute("" + myVisibility);*/
			} else
				showInternetDialogue(getApplicationContext());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		bAvailable = _sp.getBoolean("available", false);
		if (!bAvailable) {

			_bAvailCar
					.setBackgroundResource(R.drawable.unvailable_btn);
			_tvAvailable
					.setBackgroundResource(R.drawable.unavailable);
			
			/*_ed.putBoolean("available", false);
			_ed.commit();*/
		} else {

			
			_bAvailCar
					.setBackgroundResource(R.drawable.available_btn);
			_tvAvailable
					.setBackgroundResource(R.drawable.available);
			
			/*_ed.putBoolean("available", true);
			_ed.commit();*/
		}

		final LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			gps_Settings.setBackgroundResource(R.drawable.img_gps_disable);
		} else {
			gps_Settings.setBackgroundResource(R.drawable.img_gps_enable);
		}

		

	}

	

	private void setListeners() {
		// TODO Auto-generated method stub
		wifi_Connection.setOnClickListener(this);
		gps_Settings.setOnClickListener(this);
		gps_Status.setOnClickListener(this);

		_bAvailCar.setOnClickListener(this);
	}

	private void initilizeOthers() {
		// TODO Auto-generated method stub

	}

	private void initilizeViews() {
		// TODO Auto-generated method stub
		wifi_Connection = (Button) findViewById(R.id.wifi_connection_button);
		gps_Settings = (Button) findViewById(R.id.gps_button);
		gps_Status = (Button) findViewById(R.id.gps_status_button);
		_bAvailCar = (Button) findViewById(R.id.bCarButton);
		_tvAvailable = (TextView) findViewById(R.id.available_text);
		tvBusNo = (TextView) findViewById(R.id.bus_no);
		//body_layout = (LinearLayout) findViewById(R.id.layout_available);
		//ll1 = (LinearLayout) findViewById(R.id.frame_container);
	}

	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.gps_status_button) {

			final LocationManager manager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);

			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				Toast.makeText(this, "GPS is disable!", Toast.LENGTH_LONG)
						.show();
			else
				Toast.makeText(this, "GPS is Enable!", Toast.LENGTH_LONG)
						.show();

		}
		if (v.getId() == R.id.bCarButton) {

			isAvailable();
		}

		
		if (v == wifi_Connection) {

			if (!wifi.isWifiEnabled()) {
				wifi.setWifiEnabled(true);
				wifi_Connection
						.setBackgroundResource(R.drawable.img_wifi_enable);
				Toast.makeText(getApplicationContext(), "Turn ON WIFI",
						Toast.LENGTH_LONG).show();
			} else if (wifi.isWifiEnabled()) {
				wifi.setWifiEnabled(false);
				wifi_Connection
						.setBackgroundResource(R.drawable.img_wifi_disable);
				Toast.makeText(getApplicationContext(), "Turn OFF WIFI",
						Toast.LENGTH_LONG).show();
			}

		}
		if (v == gps_Settings) {

			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		
	}

		public void turnGPSOn() {

		if (android.os.Build.VERSION.SDK_INT > 11) {
			final Intent intent = new Intent(
					"android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			getApplicationContext().sendBroadcast(intent);
		}

		else {
			String provider = Settings.Secure.getString(getApplicationContext()
					.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.contains("gps")) { // if gps is enabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				getApplicationContext().sendBroadcast(poke);
			}
		}

	}

	// automatic turn off the gps
	public void turnGPSOff() {
		if (android.os.Build.VERSION.SDK_INT > 11) {
			final Intent intent = new Intent(
					"android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", false);
			getApplicationContext().sendBroadcast(intent);
		}

		else {
			String provider = Settings.Secure.getString(getApplicationContext()
					.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (provider.contains("gps")) { // if gps is enabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				getApplicationContext().sendBroadcast(poke);
			}
		}
	}

	
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void showInternetDialogue(Context context) {
		new AlertDialog.Builder(context)
				.setTitle("Internet not available")
				.setMessage("Please check your internet connection.")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	public void isAvailable(){
		
		bAvailable = _sp.getBoolean("available", false);
		if (bAvailable) {

			_bAvailCar
					.setBackgroundResource(R.drawable.unvailable_btn);
			_tvAvailable
					.setBackgroundResource(R.drawable.unavailable);
			
			_ed.putBoolean("available", false);
			_ed.commit();
		} else {

			
			_bAvailCar
					.setBackgroundResource(R.drawable.available_btn);
			_tvAvailable
					.setBackgroundResource(R.drawable.available);
			
			_ed.putBoolean("available", true);
			_ed.commit();
		}
	}
}