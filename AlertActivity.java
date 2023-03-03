package com.uck.bustrackerstudent;

import java.util.ArrayList;
import java.util.List;

import com.uck.bustrackerstudent.data.DatabaseHandler;
import com.uck.bustrackerstudent.data.MyAdapter;
import com.uck.bustrackerstudent.model.Stop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AlertActivity extends Activity {

	DatabaseHandler mDb;
	ArrayList<Stop> mStopList;
	ListView myListView;
	MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_alert);

		myListView = (ListView) findViewById(R.id.main_list);

		mStopList = new ArrayList<Stop>();

		mDb = new DatabaseHandler(this);
		
		myListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				mDb.setAlertOn(mStopList.get(arg2).getName(), 0);
				loadList();
				
				return false;
			}
		});
		loadList();
	}

	private void loadList() {
		mStopList.clear();
		mStopList = mDb.getAlertStops();
		mAdapter = new MyAdapter(this, mStopList);

		myListView.setAdapter(mAdapter);
	}

	public void onAddAlert(View view) {

		ArrayList<Stop> stops = new ArrayList<Stop>();
		stops = mDb.getStops();

		final List<String> items = new ArrayList<String>();

		for (int i = 0; i < stops.size(); i++) {

			items.add(stops.get(i).getName());

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Alert stop").setAdapter(adapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mDb.setAlertOn(items.get(which).toString(), 1);
						loadList();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
