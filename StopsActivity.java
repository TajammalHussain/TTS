package com.uck.bustrackerstudent;

import java.util.ArrayList;

import com.uck.bustrackerstudent.data.DatabaseHandler;
import com.uck.bustrackerstudent.data.MyAdapter;
import com.uck.bustrackerstudent.model.Stop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class StopsActivity extends Activity{

	DatabaseHandler mDb;
	ArrayList<Stop> mStopList;
	ListView myListView;
	MyAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_stops);
		
		mStopList = new ArrayList<Stop>();
		
		mDb = new DatabaseHandler(this);
		
		mStopList = mDb.getStops();
		
		//Log.i("Stop", mStopList.get(0).getName().toString());
	
		
		myListView = (ListView) findViewById(R.id.main_list);


		mAdapter = new MyAdapter(this, mStopList);

		myListView.setAdapter(mAdapter);
	}
}
