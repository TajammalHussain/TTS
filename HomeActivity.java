package com.uck.bustrackerstudent;

import com.uck.bustrackerstudent.services.ServiceBusesLocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {

	//ArrayList<Branch> listBranches;

	RelativeLayout rlBranch1, rlBranch2, rlBranch3, rlBranch4, rlBranch5;
	TextView txtBranch_Title_1, txtBranch_Title_2, txtBranch_Title_3,
			txtBranch_Title_4, txtBranch_Title_5;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.activity_home);
	
		startService(new Intent(getApplicationContext(),
				ServiceBusesLocation.class));

	}

	public void onLiveMapClick(View v) {

		Intent i = new Intent(this, MainActivity1.class);
		startActivity(i);
	}

	public void onStopsClick(View v) {

		Intent i = new Intent(this, StopsActivity.class);
		startActivity(i);
	}

	public void onAlertsClick(View v) {
		Intent i = new Intent(this, AlertActivity.class);
		startActivity(i);
	}

	public void onHelpClick(View v) {

		Intent i = new Intent(this, HelpActivity.class);
		startActivity(i);
	}

	public void onAboutClick(View v) {

		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}
}
