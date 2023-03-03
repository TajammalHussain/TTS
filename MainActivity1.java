package com.uck.bustrackerstudent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.uck.bustrackerstudent.model.Bus;
import com.uck.bustrackerstudent.services.ServiceBusesLocation;
import com.uck.bustrackerstudent.util.GMapV2GetRouteDirection;
import com.uck.bustrackerstudent.util.MarkerGenerator;
import com.uck.bustrackerstudent.util.ToastHelper;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * 
 * @author VIJAYAKUMAR M This class for display route current location to hotel
 *         location on google map V2
 */
public class MainActivity1 extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener{

	public static ArrayList<Bus> busList;
	List<Marker> busMarkers = new ArrayList<Marker>();
	
	List<Overlay> mapOverlays;
	
	Marker marker;
	GeoPoint point1, point2;
	LocationManager locManager;
	Drawable drawable;
	Document document;
	Document document2;
	GMapV2GetRouteDirection v2GetRouteDirection;
	LatLng fromPosition;
	LatLng toPosition;
	GoogleMap mGoogleMap;
	MarkerOptions markerOptions;
	Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mGoogleMap.setOnMapLongClickListener(this);
		mGoogleMap.setOnInfoWindowClickListener(this);
		
		v2GetRouteDirection = new GMapV2GetRouteDirection();
		
		GetRouteTask getRoute = new GetRouteTask();
		getRoute.execute();

	
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(brodcast, new IntentFilter(ServiceBusesLocation.BROADCAST_ACTION));
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	
		unregisterReceiver(brodcast);
	}
	
	private void initilizeMap() {
		if (mGoogleMap == null) {
			mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (mGoogleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}else{
				// Enabling MyLocation in Google Map
				mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.getUiSettings().setCompassEnabled(true);
				mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
				mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
				mGoogleMap.setTrafficEnabled(true);
			}
		} /*else {

			
			
			// move map to kotli
			
			mGoogleMap.addMarker(new MarkerOptions().position(kotliLatLng)
					.title("Kotli"));
			
			// mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
			
			 * markerOptions = new MarkerOptions(); fromPosition = new
			 * LatLng(11.663837, 78.147297); toPosition = new
			 * LatLng(11.723512, 78.466287);
			 
			// markerOptions.position(toPosition);
			
			// markerOptions.draggable(true);
			
			// mGoogleMap.addMarker(markerOptions);
			
			//showAllGeoFences();
		}*/
	}

	/**
	 * 
	 * @author VIJAYAKUMAR M This class Get Route on the map
	 * 
	 */
	private class GetRouteTask extends AsyncTask<String, Void, String> {

		String response = "";

		@Override
		protected void onPreExecute() {
	
		}

		@Override
		protected String doInBackground(String... urls) {
			// Get All Route values
			InputStream in = getResources().openRawResource(R.raw.route1);
			document = v2GetRouteDirection.getDocument(in);

			InputStream in2 = getResources().openRawResource(R.raw.route2);
			document2 = v2GetRouteDirection.getDocument(in2);

			response = "Success";
			return response;

		}

		@Override
		protected void onPostExecute(String result) {
			mGoogleMap.clear();
			if (response.equalsIgnoreCase("Success")) {
				ArrayList<LatLng> directionPoint = v2GetRouteDirection
						.getDirection(document);
				PolylineOptions rectLine = new PolylineOptions().width(10)
						.color(Color.RED);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}

				ArrayList<LatLng> directionPoint2 = v2GetRouteDirection
						.getDirection(document2);
				PolylineOptions rectLine2 = new PolylineOptions().width(10)
						.color(Color.RED);

				for (int i = 0; i < directionPoint2.size(); i++) {
					rectLine2.add(directionPoint2.get(i));
				}

				// Adding route on the map
				mGoogleMap.addPolyline(rectLine);
				mGoogleMap.addPolyline(rectLine2);
				
				MarkerGenerator.addStopMarkers(mGoogleMap);
				
				LatLng kotliLatLng = new LatLng(33.504516, 73.894208); // Kotli latitude and longitude
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(kotliLatLng) // Sets the center of the map to Kotli
						.zoom(13.70f) // Sets the zoom level
					// 	.bearing(90) // Sets the orientation of the camera to east
					// 	.tilt(30) // Sets the tilt of the camera to 30 degrees
						.build();
				
				
				mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); 

			}

		}

		
		
	}
	
	
	Marker bus1,bus2,bus3;
	private BroadcastReceiver brodcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			ToastHelper.showToast(getApplicationContext(), "Broadcast Recived");

			
			if(bus1 != null) {
				bus1.remove();
				bus1 = null;
	        }
			if(bus2 != null) {
				bus2.remove();
				bus2 = null;
	        }
			if(bus3 != null) {
				bus3.remove();
				bus3 = null;
	        }
		
			MarkerOptions option = new MarkerOptions();
			option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus))
			.title(busList.get(0).getBusNo())
			.position(new LatLng(busList.get(0).getLat(),busList.get(0).getLng()));
			
			bus1 = mGoogleMap.addMarker(option);
			option
			.title(busList.get(1).getBusNo())
			.position(new LatLng(busList.get(1).getLat(),busList.get(1).getLng()));
			bus2 = mGoogleMap.addMarker(option);
			
			option
			.title(busList.get(2).getBusNo())
			.position(new LatLng(busList.get(2).getLat(),busList.get(2).getLng()));
			bus3 = mGoogleMap.addMarker(option);
			 
			/*	for(int i=0; i<busList.size();i++){
				 MarkerOptions option = new MarkerOptions()
				 .title(busList.get(i).getBusNo())
				 .position(new LatLng(busList.get(i).getLat(),busList.get(i).getLng()))
	             .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
			
				 
				
				if (busMarkers.get(i) == null) {
					Marker marker = mGoogleMap.addMarker(option);
		            busMarkers.add(marker);
		        } else {
		        	
		        	option.position(new LatLng(busList.get(i).getLat(),busList.get(i).getLng()));

		        }
			}*/
			//MarkerGenerator.addBusMarkers(mGoogleMap, marker);
		}
	};

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	
}